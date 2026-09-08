package com.fotologue.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FotologueApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialization can now be handled by Hilt or in a dedicated Initializer
        com.fotologue.app.data.GearDatabase.initialize(this)
    }
}
