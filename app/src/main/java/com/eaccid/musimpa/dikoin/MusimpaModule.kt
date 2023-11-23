package com.eaccid.musimpa.dikoin

import com.eaccid.musimpa.ui.MainScreenViewModel
import com.eaccid.musimpa.repository.MoviesRepository
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val musimpaModule = module {
    single {
        MoviesRepository()
    }
    viewModel { MainScreenViewModel(get()) }
}
