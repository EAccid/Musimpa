package com.eaccid.musimpa.dikoin

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.room.Room
import com.eaccid.musimpa.LocalData
import com.eaccid.musimpa.LocalEncryptedSharedPreferences
import com.eaccid.musimpa.data.MovieRemoteMediator
import com.eaccid.musimpa.data.local.MIGRATION_1_2
import com.eaccid.musimpa.data.local.MovieDatabase
import com.eaccid.musimpa.data.local.MovieEntity
import com.eaccid.musimpa.data.remote.services.AccountApi
import com.eaccid.musimpa.data.remote.services.AuthenticationApi
import com.eaccid.musimpa.data.remote.services.MovieApi
import com.eaccid.musimpa.data.remote.services.MovieListApi
import com.eaccid.musimpa.repository.AuthenticationRepository
import com.eaccid.musimpa.repository.AuthenticationRepositoryImpl
import com.eaccid.musimpa.repository.MoviesRepository
import com.eaccid.musimpa.repository.MoviesRepositoryImpl
import com.eaccid.musimpa.ui.navigation.PreferencesDataStoreManager
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

    single<AuthenticationApi> { get<Retrofit>().create(AuthenticationApi::class.java) }
    single<AccountApi> { get<Retrofit>().create(AccountApi::class.java) }
    single<MovieApi> { get<Retrofit>().create(MovieApi::class.java) }
    single<MovieListApi> { get<Retrofit>().create(MovieListApi::class.java) }

    fun provideAuthenticationRepository(
        api: AuthenticationApi,
        pref: LocalData
    ): AuthenticationRepository {
        return AuthenticationRepositoryImpl(api, pref)
    }
    single { provideAuthenticationRepository(get(), get()) }

    fun provideMoviesRepository(
        api: MovieApi
    ): MoviesRepository {
        return MoviesRepositoryImpl(api)
    }
    single { provideMoviesRepository(get()) }
}

@OptIn(ExperimentalPagingApi::class)
val dataModule = module {
    single { PreferencesDataStoreManager(androidContext()) }
    single<LocalData> { LocalEncryptedSharedPreferences(androidContext()) }
    single<MovieDatabase> {
        Room.databaseBuilder(
            androidContext(),
            MovieDatabase::class.java,
            "movie_database",
        )
            .addMigrations(MIGRATION_1_2)
            .build()
    }
    single<Pager<Int, MovieEntity>> {
        Pager(
            config = PagingConfig(
                pageSize = 20,
                initialLoadSize = 20,  // Load only the first page
                jumpThreshold = 20 // Ensures loading happens only at the last item
            ),
            remoteMediator = MovieRemoteMediator(get(), get()),
            pagingSourceFactory = { get<MovieDatabase>().movieDao.pagingSource() }
        )
    }
}

val viewModelsModule = module {
    viewModel { MainScreenViewModel(get()) }
    viewModel { MovieListScreenViewModel(get(), get()) }
    viewModel { MovieDetailsScreenViewModel(get(), get()) }
}
