package com.fotologue.app.util

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

enum class SafelightMode {
    AUTO, ON, OFF
}

class DarkroomLightGuard(
    context: Context,
    private val onAutoSafelightChanged: (Boolean) -> Unit,
    private val onWhiteLightWarning: (Boolean) -> Unit = {}
) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
    private val lightSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_LIGHT)
    
    private var isListening = false
    private var currentMode = SafelightMode.OFF

    fun updateMode(mode: SafelightMode) {
        currentMode = mode
        if (mode == SafelightMode.AUTO) {
            registerListener()
        } else {
            unregisterListener()
            onWhiteLightWarning(false)
            if (mode == SafelightMode.ON) {
                onAutoSafelightChanged(true)
            } else {
                onAutoSafelightChanged(false)
            }
        }
    }

    fun registerListener() {
        if (!isListening && lightSensor != null && currentMode == SafelightMode.AUTO) {
            sensorManager?.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)
            isListening = true
        }
    }

    fun unregisterListener() {
        if (isListening) {
            sensorManager?.unregisterListener(this)
            isListening = false
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null || currentMode != SafelightMode.AUTO) return
        if (event.sensor.type == Sensor.TYPE_LIGHT) {
            val lux = event.values[0]
            if (lux <= 3.0f) {
                // Dark environment -> Turn ON Safelight Red
                onAutoSafelightChanged(true)
                onWhiteLightWarning(false)
            } else if (lux >= 20.0f) {
                // Bright ambient light -> Turn OFF Safelight Red
                onAutoSafelightChanged(false)
                onWhiteLightWarning(lux >= 10.0f)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
