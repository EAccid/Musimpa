package com.eaccid.musimpa.dikoin

import com.eaccid.musimpa.LocalData
import com.eaccid.musimpa.LocalSharedPreferences
import com.eaccid.musimpa.network.TMDbServiceAPI
import com.eaccid.musimpa.repository.AuthenticationRepository
import com.eaccid.musimpa.repository.AuthenticationRepositoryImpl
import com.eaccid.musimpa.ui.mainscreen.MainScreenViewModel
import com.eaccid.musimpa.ui.movieslist.ui.theme.MoviesScreenViewModel
import com.eaccid.musimpa.utils.BASE_URL
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

val repositoryModule = module {

    fun provideAuthenticationRepository(
        api: TMDbServiceAPI,
        pref: LocalData
    ): AuthenticationRepository {
        return AuthenticationRepositoryImpl(api, pref)
    }
    single { provideAuthenticationRepository(get(), get()) }
    //network
    single<Retrofit> {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }
    fun provideTMDbServiceAPI(retrofit: Retrofit): TMDbServiceAPI {
        return retrofit.create(TMDbServiceAPI::class.java)
    }
    single { provideTMDbServiceAPI(get()) }
    single<LocalData> { LocalSharedPreferences(androidContext()) }
}


//todo split modules
val musimpaModule = module {

    viewModel { MainScreenViewModel(get()) }
    viewModel { MoviesScreenViewModel() }

}
