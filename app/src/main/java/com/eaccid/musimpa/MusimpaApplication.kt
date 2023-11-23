package com.eaccid.musimpa

import android.app.Application
import com.eaccid.musimpa.dikoin.musimpaModule
import org.koin.core.context.GlobalContext.startKoin

class MusimpaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            // declare modules
            modules(getModules())
        }
    }

    private fun getModules() = listOf(musimpaModule)
}