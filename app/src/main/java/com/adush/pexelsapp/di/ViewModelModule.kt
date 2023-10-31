package com.adush.pexelsapp.di

import androidx.lifecycle.ViewModel
import com.adush.pexelsapp.ui.details.DetailsViewModel
import com.adush.pexelsapp.ui.home.HomeViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    fun bindHomeViewModel(viewModel: HomeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DetailsViewModel::class)
    fun bindDetailsFragment(viewModel: DetailsViewModel): ViewModel
}