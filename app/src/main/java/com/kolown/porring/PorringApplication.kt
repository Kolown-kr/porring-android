package com.kolown.porring

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PorringApplication : Application(){
    companion object {
        private lateinit var instance: PorringApplication
        fun getApplicationContext(): PorringApplication = instance
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

}







