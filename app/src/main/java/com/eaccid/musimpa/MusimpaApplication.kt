package com.eaccid.musimpa

import android.app.Application
import androidx.work.Configuration
import com.eaccid.musimpa.dikoin.KoinWorkerFactory
import com.eaccid.musimpa.dikoin.dataModule
import com.eaccid.musimpa.dikoin.repositoryModule
import com.eaccid.musimpa.dikoin.useCaseModule
import com.eaccid.musimpa.dikoin.viewModelsModule
import com.eaccid.musimpa.dikoin.workerModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import org.koin.java.KoinJavaComponent.getKoin

class MusimpaApplication : Application(), Configuration.Provider {

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(getKoin().get<KoinWorkerFactory>())
            .build()

    override fun onCreate() {
        super.onCreate()
        startKoin {
            //koin declare modules
            androidContext(this@MusimpaApplication)
            modules(getModules())
        }
    }

    private fun getModules() = listOf(repositoryModule, dataModule, useCaseModule, viewModelsModule, workerModule)

}