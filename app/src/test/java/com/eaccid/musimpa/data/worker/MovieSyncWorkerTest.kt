package com.eaccid.musimpa.data.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.eaccid.musimpa.domain.usecase.SyncPopularMoviesUseCase
import io.mockk.coEvery
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Before
import kotlin.test.Test

class MovieSyncWorkerTest {

    private lateinit var context: Context
    private lateinit var workerParams: WorkerParameters
    private lateinit var syncPopularMoviesUseCase: SyncPopularMoviesUseCase
    private lateinit var worker: MovieSyncWorker

    @Before
    fun setUp() {
        context = mockk(relaxed = true)
        workerParams = mockk(relaxed = true)
        syncPopularMoviesUseCase = mockk()
        worker = MovieSyncWorker(context, workerParams, syncPopularMoviesUseCase)
    }

    @Test
    fun `doWork returns success when use case completes`() = runTest {
        coEvery { syncPopularMoviesUseCase() } just runs

        val result = worker.doWork()

        assertEquals(ListenableWorker.Result.success(), result)
    }

    @Test
    fun `doWork returns retry when use case throws`() = runTest {
        coEvery { syncPopularMoviesUseCase() } throws RuntimeException("Network error")

        val result = worker.doWork()

        assertEquals(ListenableWorker.Result.retry(), result)
    }
}