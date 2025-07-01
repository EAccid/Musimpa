package com.eaccid.musimpa

import android.app.Application
import com.eaccid.musimpa.data.worker.scheduleMovieSync
import com.eaccid.musimpa.dikoin.dataModule
import com.eaccid.musimpa.dikoin.repositoryModule
import com.eaccid.musimpa.dikoin.useCaseModule
import com.eaccid.musimpa.dikoin.viewModelsModule
import com.eaccid.musimpa.dikoin.workerModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.component.KoinComponent
import org.koin.core.context.GlobalContext.startKoin

class MusimpaApplication : Application(), KoinComponent {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@MusimpaApplication)
            modules(getModules())
            workManagerFactory()
        }
        scheduleMovieSync(this) //for testing reason it is scheduled here
    }

    private fun getModules() =
        listOf(repositoryModule, dataModule, useCaseModule, workerModule, viewModelsModule)
}