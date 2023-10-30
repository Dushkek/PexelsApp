package com.adush.pexelsapp.di

import android.app.Application
import com.adush.pexelsapp.data.network.ApiFactory
import com.adush.pexelsapp.data.network.ApiService
import com.adush.pexelsapp.data.repository.ImageRepositoryImpl
import com.adush.pexelsapp.domain.ImageRepository
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
interface DataModule {

    @Binds
    @ApplicationScope
    fun bindRepository(impl: ImageRepositoryImpl): ImageRepository

    companion object {

        @Provides
        @ApplicationScope
        fun provideApiService(application: Application): ApiService {
            return ApiFactory().getApiService(application)
        }
    }
}