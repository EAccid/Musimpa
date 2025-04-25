package com.eaccid.musimpa

import android.app.Application
import com.eaccid.musimpa.dikoin.dataModule
import com.eaccid.musimpa.dikoin.repositoryModule
import com.eaccid.musimpa.dikoin.viewModelsModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class MusimpaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            //koin declare modules
            androidContext(this@MusimpaApplication)
            modules(getModules())
        }
    }

    private fun getModules() = listOf(repositoryModule, dataModule, viewModelsModule)
}