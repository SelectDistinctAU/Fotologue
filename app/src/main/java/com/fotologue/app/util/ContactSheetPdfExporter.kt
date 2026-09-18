package com.fotologue.app.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import android.net.Uri
import com.fotologue.core.data.LogEntryWithGear
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ContactSheetPdfExporter {

    private const val PAGE_WIDTH = 595 // A4 width in points at 72 DPI
    private const val PAGE_HEIGHT = 842 // A4 height in points at 72 DPI
    private const val MARGIN = 24

    fun generatePdf(
        context: Context,
        logs: List<LogEntryWithGear>,
        outputStream: OutputStream,
        creator: String = "",
        copyright: String = ""
    ): Boolean {
        if (logs.isEmpty()) return false

        val pdfDoc = PdfDocument()
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        val firstLog = logs.first()
        val cameraName = firstLog.camera?.name ?: "Unknown Camera"
        val filmName = firstLog.film?.name ?: "Unknown Film"
        val format = firstLog.film?.format ?: firstLog.camera?.format ?: "35mm"

        val rollParts = (firstLog.logEntry.filmRoll ?: "1.01").split(".")
        val rollNumber = rollParts.getOrNull(0)?.toIntOrNull() ?: 1

        val columns = when {
            format.contains("120", ignoreCase = true) -> 3
            format.contains("Large", ignoreCase = true) -> 2
            else -> 6 // 35mm default
        }

        val availableWidth = PAGE_WIDTH - (MARGIN * 2)
        val gap = 8
        val cellWidth = (availableWidth - (gap * (columns - 1))) / columns
        val cellHeight = cellWidth + 24 // Aspect ratio box + metadata space

        val headerHeight = 90
        val footerHeight = 30
        val availableHeight = PAGE_HEIGHT - headerHeight - footerHeight - (MARGIN * 2)

        val rowsPerPage = (availableHeight / (cellHeight + gap)).coerceAtLeast(1)
        val itemsPerPage = columns * rowsPerPage

        val totalPages = (logs.size + itemsPerPage - 1) / itemsPerPage
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

        for (pageIndex in 0 until totalPages) {
            val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageIndex + 1).create()
            val page = pdfDoc.startPage(pageInfo)
            val canvas = page.canvas

            // Dark darkroom background
            canvas.drawColor(Color.parseColor("#181818"))

            // Header Section
            paint.color = Color.parseColor("#E0E0E0")
            paint.textSize = 18f
            paint.isFakeBoldText = true
            canvas.drawText("FOTOLOGUE • FILM PROOF SHEET", MARGIN.toFloat(), MARGIN + 22f, paint)

            paint.textSize = 10f
            paint.isFakeBoldText = false
            paint.color = Color.parseColor("#A0A0A0")
            val subTitle = "$cameraName • $filmName (Roll #$rollNumber) | ${logs.size} Exposures"
            canvas.drawText(subTitle, MARGIN.toFloat(), MARGIN + 40f, paint)

            val metaRight = "$creator ${if (copyright.isNotBlank()) "| $copyright" else ""}"
            val metaWidth = paint.measureText(metaRight)
            canvas.drawText(metaRight, PAGE_WIDTH - MARGIN - metaWidth, MARGIN + 40f, paint)

            // Header Divider Line
            paint.color = Color.parseColor("#333333")
            paint.strokeWidth = 1f
            canvas.drawLine(MARGIN.toFloat(), MARGIN + 52f, (PAGE_WIDTH - MARGIN).toFloat(), MARGIN + 52f, paint)

            // Grid Items Drawing
            val startIndex = pageIndex * itemsPerPage
            val endIndex = minOf(startIndex + itemsPerPage, logs.size)

            for (i in startIndex until endIndex) {
                val itemIndexOnPage = i - startIndex
                val col = itemIndexOnPage % columns
                val row = itemIndexOnPage / columns

                val x = MARGIN + col * (cellWidth + gap)
                val y = MARGIN + headerHeight + row * (cellHeight + gap)

                val item = logs[i]
                val log = item.logEntry
                val (rollNo, frameNo) = parseRollAndFrame(log.filmRoll)

                // Card background
                paint.style = Paint.Style.FILL
                paint.color = Color.parseColor("#242424")
                val cardRect = RectF(x.toFloat(), y.toFloat(), (x + cellWidth).toFloat(), (y + cellHeight).toFloat())
                canvas.drawRoundRect(cardRect, 4f, 4f, paint)

                // Card outline border
                paint.style = Paint.Style.STROKE
                paint.color = Color.parseColor("#3A3A3A")
                canvas.drawRoundRect(cardRect, 4f, 4f, paint)

                // Frame rebate badge `#01`
                paint.style = Paint.Style.FILL
                paint.color = Color.parseColor("#FFD54F")
                paint.textSize = 9f
                paint.isFakeBoldText = true
                val badgeText = "#${String.format(Locale.US, "%02d", frameNo)}"
                canvas.drawText(badgeText, x + 6f, y + 14f, paint)

                // Thumbnail image rendering
                val imgTop = y + 18
                val imgHeight = cellWidth - 12
                val imgRect = RectF(x + 6f, imgTop.toFloat(), x + cellWidth - 6f, (imgTop + imgHeight).toFloat())

                if (!log.exposureSettings.thumbnailUri.isNullOrEmpty()) {
                    var bitmap: Bitmap? = null
                    try {
                        val uri = Uri.parse(log.exposureSettings.thumbnailUri)
                        context.contentResolver.openInputStream(uri)?.use { input ->
                            bitmap = BitmapFactory.decodeStream(input)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    if (bitmap != null) {
                        val srcRect = Rect(0, 0, bitmap!!.width, bitmap!!.height)
                        canvas.drawBitmap(bitmap!!, srcRect, imgRect, null)
                        bitmap!!.recycle()
                    } else {
                        drawPlaceholder(canvas, imgRect, paint)
                    }
                } else {
                    drawPlaceholder(canvas, imgRect, paint)
                }

                // Exposure Metadata text
                paint.style = Paint.Style.FILL
                paint.color = Color.parseColor("#D0D0D0")
                paint.textSize = 7.5f
                paint.isFakeBoldText = false
                val expText = "${log.exposureSettings.shutterSpeed} @ ${log.exposureSettings.aperture}"
                canvas.drawText(expText, x + 6f, y + cellHeight - 6f, paint)
            }

            // Footer Section
            paint.color = Color.parseColor("#666666")
            paint.textSize = 8f
            val dateStr = "Generated ${dateFormat.format(Date())} via Fotologue"
            canvas.drawText(dateStr, MARGIN.toFloat(), PAGE_HEIGHT - MARGIN.toFloat(), paint)

            val pageStr = "Page ${pageIndex + 1} of $totalPages"
            val pageStrWidth = paint.measureText(pageStr)
            canvas.drawText(pageStr, PAGE_WIDTH - MARGIN - pageStrWidth, PAGE_HEIGHT - MARGIN.toFloat(), paint)

            pdfDoc.finishPage(page)
        }

        return try {
            pdfDoc.writeTo(outputStream)
            pdfDoc.close()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            pdfDoc.close()
            false
        }
    }

    private fun drawPlaceholder(canvas: Canvas, rect: RectF, paint: Paint) {
        paint.style = Paint.Style.FILL
        paint.color = Color.parseColor("#1A1A1A")
        canvas.drawRect(rect, paint)

        paint.style = Paint.Style.STROKE
        paint.color = Color.parseColor("#333333")
        canvas.drawRect(rect, paint)
    }

    private fun parseRollAndFrame(filmRollStr: String?): Pair<Int, Int> {
        if (filmRollStr.isNullOrBlank()) return Pair(1, 1)
        if (filmRollStr.contains(".")) {
            val parts = filmRollStr.split(".")
            val roll = parts.getOrNull(0)?.toIntOrNull() ?: 1
            val frame = parts.getOrNull(1)?.toIntOrNull() ?: 1
            return Pair(roll, frame)
        }
        val numbers = Regex("""\d+""").findAll(filmRollStr).map { it.value.toInt() }.toList()
        val roll = numbers.getOrNull(0) ?: 1
        val frame = numbers.getOrNull(1) ?: 1
        return Pair(roll, frame)
    }
}
