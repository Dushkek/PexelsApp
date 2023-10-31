package com.adush.pexelsapp.di

import android.app.Application
import com.adush.pexelsapp.ui.details.DetailsFragment
import com.adush.pexelsapp.ui.home.HomeFragment
import dagger.BindsInstance
import dagger.Component

@ApplicationScope
@Component(modules = [DataModule::class, ViewModelModule::class])
interface ApplicationComponent {

    fun inject(fragment: HomeFragment)

    fun inject(fragment: DetailsFragment)

    @Component.Factory
    interface ApplicationComponentFactory {

        fun create(
            @BindsInstance application: Application
        ): ApplicationComponent
    }
}