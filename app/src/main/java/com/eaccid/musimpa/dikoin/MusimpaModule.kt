package com.eaccid.musimpa.dikoin

import com.eaccid.musimpa.LocalData
import com.eaccid.musimpa.LocalEncryptedSharedPreferences
import com.eaccid.musimpa.data.remote.TmdbServiceAPI
import com.eaccid.musimpa.repository.AuthenticationRepository
import com.eaccid.musimpa.repository.AuthenticationRepositoryImpl
import com.eaccid.musimpa.repository.MoviesRepository
import com.eaccid.musimpa.repository.MoviesRepositoryImpl
import com.eaccid.musimpa.ui.PreferencesDataStoreManager
import com.eaccid.musimpa.ui.mainscreen.MainScreenViewModel
import com.eaccid.musimpa.ui.moviedetailsscreen.MovieDetailsScreenViewModel
import com.eaccid.musimpa.ui.movielistscreen.MovieListScreenViewModel
import com.eaccid.musimpa.utils.BASE_URL
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

val repositoryModule = module {
    single {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }
    single<Retrofit> {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(get()))
            .build()
    }

    fun provideTMDbServiceAPI(retrofit: Retrofit): TmdbServiceAPI {
        return retrofit.create(TmdbServiceAPI::class.java)
    }
    single { provideTMDbServiceAPI(get()) }

    fun provideAuthenticationRepository(
        api: TmdbServiceAPI,
        pref: LocalData
    ): AuthenticationRepository {
        return AuthenticationRepositoryImpl(api, pref)
    }
    single { provideAuthenticationRepository(get(), get()) }

    fun provideMoviesRepository(
        api: TmdbServiceAPI,
        pref: LocalData
    ): MoviesRepository {
        return MoviesRepositoryImpl(api, pref)
    }
    single { provideMoviesRepository(get(), get()) }
}

val dataModule = module {
    single { PreferencesDataStoreManager(androidContext()) }
    single<LocalData> { LocalEncryptedSharedPreferences(androidContext()) }
}

val navigationModule = module {
//    single<Navigator> { DefaultNavigator(get()) }
}

val viewModelsModule = module {

    viewModel { MainScreenViewModel(get()) }
    viewModel { MovieListScreenViewModel(get()) }
    viewModel { MovieDetailsScreenViewModel(get(), get()) }

}
