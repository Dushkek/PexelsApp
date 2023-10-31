package com.adush.pexelsapp.ui.progress

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class ProgressViewModel: ViewModel() {
    val showProgress by lazy { MutableLiveData(false) }

    fun showProgress() {
        showProgress.value = true
    }

    fun hideProgress() {
        showProgress.value = false
    }
}