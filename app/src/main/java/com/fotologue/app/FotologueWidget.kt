package com.fotologue.app

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews

import android.app.PendingIntent
import android.content.Intent
import com.fotologue.core.data.FotologueRepository
import com.fotologue.core.data.SettingsRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.util.Locale

@AndroidEntryPoint
class FotologueWidget : AppWidgetProvider() {
    
    @Inject
    lateinit var settingsRepository: SettingsRepository
    
    @Inject
    lateinit var fotologueRepository: FotologueRepository

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val cameraId = settingsRepository.lastCameraId.first()
                val filmId = settingsRepository.lastFilmId.first()
                val rollNum = settingsRepository.currentRoll.first()
                val frameNum = settingsRepository.currentFrame.first()

                var titleStr = "No Active Kit"
                if (cameraId != null) {
                    val camName = fotologueRepository.getCameraById(cameraId).first()?.name ?: "Camera"
                    val filmName = filmId?.let { fotologueRepository.getFilmById(it).first()?.name }
                    titleStr = if (filmName != null) "$camName • $filmName" else camName
                }

                val frameStr = String.format(Locale.US, "Roll %d.%02d", rollNum, frameNum)

                for (appWidgetId in appWidgetIds) {
                    updateAppWidget(context, appWidgetManager, appWidgetId, titleStr, frameStr)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int,
    kitTitle: String,
    frameSubtitle: String
) {
    val views = RemoteViews(context.packageName, R.layout.fotologue_widget)
    views.setTextViewText(R.id.widget_camera_film_text, kitTitle)
    views.setTextViewText(R.id.widget_frame_text, frameSubtitle)

    val addLogIntent = Intent(context, MainActivity::class.java).apply {
        action = "com.fotologue.ACTION_ADD_LOG"
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }
    val addLogPendingIntent = PendingIntent.getActivity(
        context,
        0,
        addLogIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val meterIntent = Intent(context, MainActivity::class.java).apply {
        action = "com.fotologue.ACTION_LIGHT_METER"
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }
    val meterPendingIntent = PendingIntent.getActivity(
        context,
        1,
        meterIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    views.setOnClickPendingIntent(R.id.widget_add_button, addLogPendingIntent)
    views.setOnClickPendingIntent(R.id.widget_container, addLogPendingIntent)
    views.setOnClickPendingIntent(R.id.widget_meter_button, meterPendingIntent)

    appWidgetManager.updateAppWidget(appWidgetId, views)
}