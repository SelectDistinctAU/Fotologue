package com.fotologue.app.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface
import android.net.Uri
import com.fotologue.app.data.LogEntryWithGear
import com.fotologue.app.data.PictureExportSize
import com.fotologue.app.data.Tag
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
        contact: String = ""
    ) {
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.US)
        val usedNames = mutableSetOf<String>()
        
        ZipOutputStream(outputStream.buffered()).use { zos ->
            // ...
            // (Skipping metadata exports as they are fine)
            // ... (keep metadata.json and metadata.csv logic)
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

            // 3. thumbnails/
            logs.forEach { entry ->
                val uriString = entry.logEntry.exposureSettings.thumbnailUri
                if (!uriString.isNullOrEmpty()) {
                    try {
                        val uri = Uri.parse(uriString)
                        
                        // New Naming Convention: yyyymmdd_FilmAbbr_Roll###_Frame##
                        val datePrefix = dateFormat.format(Date(entry.logEntry.timestamp))
                        val filmAbbr = entry.film?.abbreviation?.ifBlank { entry.film?.name?.take(3)?.uppercase() } ?: "UNK"
                        
                        val rollParts = (entry.logEntry.filmRoll ?: "0.00").split(".")
                        val rollNo = rollParts.getOrNull(0)?.toIntOrNull() ?: 0
                        val frameNo = rollParts.getOrNull(1)?.toIntOrNull() ?: 0
                        
                        var baseName = String.format(Locale.US, "%s_%s_R%03d_F%02d", datePrefix, filmAbbr, rollNo, frameNo)
                        
                        // Collision avoidance
                        if (usedNames.contains(baseName)) {
                            baseName += "_${entry.logEntry.id}"
                        }
                        usedNames.add(baseName)
                        
                        val fileName = baseName + ".jpg"
                        
                        // Check if file exists and is readable before creating zip entry
                        val isAccessible = try {
                            context.contentResolver.openInputStream(uri)?.use { true } ?: false
                        } catch (e: Exception) {
                            false
                        }

                        if (isAccessible) {
                            // 3a. image.jpg
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

                            // 3b. image.xmp (ISO 16684 sidecar)
                            try {
                                val xmpFileName = fileName.substringBeforeLast(".") + ".xmp"
                                val xmpContent = LogEntryXmpExporter.toXmp(entry, allTags, creator, copyright, contact)
                                zos.putNextEntry(ZipEntry("thumbnails/$xmpFileName"))
                                zos.write(xmpContent.toByteArray())
                                zos.closeEntry()
                            } catch (e: Exception) {
                                e.printStackTrace()
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

        // 1. Get EXIF Orientation
        val orientation = try {
            context.contentResolver.openInputStream(uri)?.use { input ->
                ExifInterface(input).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            } ?: ExifInterface.ORIENTATION_NORMAL
        } catch (e: Exception) {
            ExifInterface.ORIENTATION_NORMAL
        }

        val isSwapped = orientation == ExifInterface.ORIENTATION_ROTATE_90 || orientation == ExifInterface.ORIENTATION_ROTATE_270

        // 2. First decode with inJustDecodeBounds=true to check dimensions
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

        // Adjusted dimensions for scene orientation
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

        // 3. Decode bitmap with inSampleSize set
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
            // 4. Rotate bitmap if needed
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

            // 5. Scale to fit target dimensions
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
