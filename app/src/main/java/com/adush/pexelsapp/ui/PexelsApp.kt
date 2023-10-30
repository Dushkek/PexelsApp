package com.adush.pexelsapp.ui

import android.app.Application
import com.adush.pexelsapp.di.DaggerApplicationComponent

class PexelsApp: Application() {

    val component by lazy {
        DaggerApplicationComponent.factory()
            .create(this)
    }
}