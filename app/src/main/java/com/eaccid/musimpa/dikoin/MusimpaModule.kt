package com.eaccid.musimpa.dikoin

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.room.Room
import com.eaccid.musimpa.data.local.LocalData
import com.eaccid.musimpa.data.local.LocalEncryptedSharedPreferences
import com.eaccid.musimpa.data.local.room.MIGRATION_1_2
import com.eaccid.musimpa.data.local.room.MovieDatabase
import com.eaccid.musimpa.data.paging.MovieRemoteMediator
import com.eaccid.musimpa.data.remote.services.AccountApi
import com.eaccid.musimpa.data.remote.services.AuthenticationApi
import com.eaccid.musimpa.data.remote.services.MovieApi
import com.eaccid.musimpa.data.remote.services.MovieListApi
import com.eaccid.musimpa.data.remote.services.interceptors.KeyLanguageQueryInterceptor
import com.eaccid.musimpa.data.worker.MovieSyncWorker
import com.eaccid.musimpa.domain.repository.AuthenticationRepository
import com.eaccid.musimpa.domain.repository.AuthenticationRepositoryImpl
import com.eaccid.musimpa.domain.repository.MoviesRepository
import com.eaccid.musimpa.domain.repository.MoviesRepositoryImpl
import com.eaccid.musimpa.domain.usecase.SyncPopularMoviesUseCase
import com.eaccid.musimpa.ui.mainscreen.MainScreenViewModel
import com.eaccid.musimpa.ui.moviedetailsscreen.MovieDetailsScreenViewModel
import com.eaccid.musimpa.ui.movielistscreen.MovieListScreenViewModel
import com.eaccid.musimpa.ui.navigation.PreferencesDataStoreManager
import com.eaccid.musimpa.utils.BASE_URL
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.workmanager.dsl.worker
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

val repositoryModule = module {
    single {
        OkHttpClient.Builder()
            .addInterceptor(KeyLanguageQueryInterceptor())
            .build()
    }
    single {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }
    single<Retrofit> {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(/*okHttpClient*/ get())
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
        api: MovieApi, movieDb: MovieDatabase
    ): MoviesRepository {
        return MoviesRepositoryImpl(api, movieDb)
    }
    single { provideMoviesRepository(get(), get<MovieDatabase>()) }
}

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
}

@OptIn(ExperimentalPagingApi::class)
val viewModelsModule = module {
    viewModel { MainScreenViewModel(get()) }
    viewModel {
        val repository: MoviesRepository = get()
        val movieDb: MovieDatabase = get()
        MovieListScreenViewModel(
            createPager = {
                Pager(
                    config = PagingConfig(pageSize = 20),
                    remoteMediator = MovieRemoteMediator(
                        moviesRepository = repository
                    ),
                    pagingSourceFactory = {
                        movieDb.movieDao.pagingSource()
                    }
                )
            }
        )
    }
    viewModel { MovieDetailsScreenViewModel(get(), get()) }
}

val useCaseModule = module {
    single { SyncPopularMoviesUseCase(get()) }
}

val workerModule = module {
    worker(named<MovieSyncWorker>()) { MovieSyncWorker(get(), get(), get()) }
    factory { KoinWorkerFactory(get()) }
}