package com.fotologue.app.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface
import android.net.Uri
import com.fotologue.core.data.LogEntryWithGear
import com.fotologue.core.data.MacroCopyNamingOptions
import com.fotologue.core.data.PictureExportSize
import com.fotologue.core.data.Tag
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object LogEntryZipExporter {
    fun toZip(
        context: Context,
        logs: List<LogEntryWithGear>,
        outputStream: OutputStream,
        exportSize: PictureExportSize = PictureExportSize.NATURAL,
        allTags: List<Tag> = emptyList(),
        creator: String = "",
        copyright: String = "",
        contact: String = "",
        macroCopyOptions: MacroCopyNamingOptions = MacroCopyNamingOptions()
    ) {
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.US)
        val usedNames = mutableSetOf<String>()
        
        ZipOutputStream(outputStream.buffered()).use { zos ->
            // 1. metadata.json
            try {
                val jsonExport = LogEntryJsonExporter.toJson(logs)
                zos.putNextEntry(ZipEntry("metadata.json"))
                zos.write(jsonExport.toByteArray())
                zos.closeEntry()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // 2. metadata.csv
            try {
                val csvExport = LogEntryExporter.toCsv(logs)
                zos.putNextEntry(ZipEntry("metadata.csv"))
                zos.write(csvExport.toByteArray())
                zos.closeEntry()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // 3. sidecars/ & thumbnails/
            logs.forEachIndexed { index, entry ->
                val uriString = entry.logEntry.exposureSettings.thumbnailUri
                
                var baseName = if (macroCopyOptions.enabled) {
                    macroCopyOptions.formatFileName(index, logs.size)
                } else {
                    val datePrefix = dateFormat.format(Date(entry.logEntry.timestamp))
                    val filmAbbr = entry.film?.abbreviation?.ifBlank { entry.film?.name?.take(3)?.uppercase() } ?: "UNK"
                    
                    val rollParts = (entry.logEntry.filmRoll ?: "0.00").split(".")
                    val rollNo = rollParts.getOrNull(0)?.toIntOrNull() ?: 0
                    val frameNo = rollParts.getOrNull(1)?.toIntOrNull() ?: 0
                    
                    String.format(Locale.US, "%s_%s_R%03d_F%02d", datePrefix, filmAbbr, rollNo, frameNo)
                }

                if (usedNames.contains(baseName)) {
                    baseName += "_${entry.logEntry.id}"
                }
                usedNames.add(baseName)

                // 3a. Sidecar XMP file
                try {
                    val xmpFileName = "sidecars/$baseName.xmp"
                    val xmpContent = LogEntryXmpExporter.toXmp(entry, allTags, creator, copyright, contact)
                    zos.putNextEntry(ZipEntry(xmpFileName))
                    zos.write(xmpContent.toByteArray())
                    zos.closeEntry()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                // 3b. Thumbnail image
                if (!uriString.isNullOrEmpty()) {
                    try {
                        val uri = Uri.parse(uriString)
                        val fileName = "$baseName.jpg"
                        
                        val isAccessible = try {
                            context.contentResolver.openInputStream(uri)?.use { true } ?: false
                        } catch (e: Exception) {
                            false
                        }

                        if (isAccessible) {
                            zos.putNextEntry(ZipEntry("thumbnails/$fileName"))
                            try {
                                if (exportSize == PictureExportSize.SPACE_SAVER) {
                                    resizeAndWrite(context, uri, zos)
                                } else {
                                    context.contentResolver.openInputStream(uri)?.use { input ->
                                        input.copyTo(zos)
                                    }
                                }
                            } finally {
                                zos.closeEntry()
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            zos.finish()
        }
    }

    private fun resizeAndWrite(context: Context, uri: Uri, outputStream: OutputStream) {
        val baseTargetWidth = 1280
        val baseTargetHeight = 720

        val orientation = try {
            context.contentResolver.openInputStream(uri)?.use { input ->
                ExifInterface(input).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            } ?: ExifInterface.ORIENTATION_NORMAL
        } catch (e: Exception) {
            ExifInterface.ORIENTATION_NORMAL
        }

        val isSwapped = orientation == ExifInterface.ORIENTATION_ROTATE_90 || orientation == ExifInterface.ORIENTATION_ROTATE_270

        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        try {
            context.contentResolver.openInputStream(uri)?.use { input ->
                BitmapFactory.decodeStream(input, null, options)
            }
        } catch (e: Exception) {
            return
        }

        if (options.outWidth <= 0 || options.outHeight <= 0) return

        val sceneWidth = if (isSwapped) options.outHeight else options.outWidth
        val sceneHeight = if (isSwapped) options.outWidth else options.outHeight
        
        val isPortrait = sceneHeight > sceneWidth
        val targetWidth = if (isPortrait) baseTargetHeight else baseTargetWidth
        val targetHeight = if (isPortrait) baseTargetWidth else baseTargetHeight

        var inSampleSize = 1
        if (sceneHeight > targetHeight || sceneWidth > targetWidth) {
            val halfHeight = sceneHeight / 2
            val halfWidth = sceneWidth / 2
            while (halfHeight / inSampleSize >= targetHeight && halfWidth / inSampleSize >= targetWidth) {
                inSampleSize *= 2
            }
        }

        val decodeOptions = BitmapFactory.Options().apply {
            this.inSampleSize = inSampleSize
        }
        var bitmap = try {
            context.contentResolver.openInputStream(uri)?.use { input ->
                BitmapFactory.decodeStream(input, null, decodeOptions)
            }
        } catch (e: Exception) {
            null
        } ?: return

        try {
            if (orientation != ExifInterface.ORIENTATION_NORMAL) {
                val matrix = Matrix()
                when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                    ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                    ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
                    ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.postScale(-1f, 1f)
                    ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.postScale(1f, -1f)
                }
                val rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                if (rotated != bitmap) {
                    bitmap.recycle()
                    bitmap = rotated
                }
            }

            val scaledBitmap = if (bitmap.width > targetWidth || bitmap.height > targetHeight) {
                val ratio = Math.min(targetWidth.toFloat() / bitmap.width, targetHeight.toFloat() / bitmap.height)
                val destWidth = Math.max(1, (bitmap.width * ratio).toInt())
                val destHeight = Math.max(1, (bitmap.height * ratio).toInt())
                Bitmap.createScaledBitmap(bitmap, destWidth, destHeight, true)
            } else {
                bitmap
            }

            try {
                val bos = ByteArrayOutputStream()
                if (scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 85, bos)) {
                    outputStream.write(bos.toByteArray())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                if (scaledBitmap != bitmap) {
                    scaledBitmap.recycle()
                }
            }
        } finally {
            if (!bitmap.isRecycled) {
                bitmap.recycle()
            }
        }
    }
}
