package com.eaccid.musimpa

import com.eaccid.musimpa.data.local.LocalData
import com.eaccid.musimpa.data.remote.services.AccountApi
import com.eaccid.musimpa.data.remote.services.AuthenticationApi
import com.eaccid.musimpa.data.remote.services.MovieApiService
import com.eaccid.musimpa.data.remote.services.MovieListApi
import com.eaccid.musimpa.dikoin.repositoryModule
import com.eaccid.musimpa.data.repository.AuthenticationRepository
import com.eaccid.musimpa.data.repository.MoviesRepository
import io.mockk.mockk
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.core.component.get
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import kotlin.test.assertNotNull

class CheckModulesTest : KoinTest {

    private val testModule = module {
        single<AuthenticationApi> { mockk(relaxed = true) }
        single<AccountApi> { mockk(relaxed = true) }
        single<MovieApiService> { mockk(relaxed = true) }
        single<MovieListApi> { mockk(relaxed = true) }
        single<LocalData> { mockk(relaxed = true) }
    }

    @Before
    fun setUp() {
        startKoin {
            modules(listOf(repositoryModule, testModule))
        }
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `authentication repository is resolved`() {
        val repo = get<AuthenticationRepository>()
        assertNotNull(repo)
    }

    @Test
    fun `movies repository is resolved`() {
        val repo = get<MoviesRepository>()
        assertNotNull(repo)
    }

}