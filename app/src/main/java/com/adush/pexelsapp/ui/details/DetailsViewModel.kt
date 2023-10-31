package com.adush.pexelsapp.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.adush.pexelsapp.domain.GetImageByIdUseCase
import com.adush.pexelsapp.domain.Response
import com.adush.pexelsapp.domain.model.ImageItem
import com.adush.pexelsapp.ui.progress.ProgressViewModel
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class DetailsViewModel @Inject constructor(
    private val getImageByIdUseCase: GetImageByIdUseCase
): ProgressViewModel() {

    private val _image = MutableLiveData<ImageItem>()
    val image: LiveData<ImageItem>
        get() = _image

    private val _errorMessage = MutableLiveData<Unit>()
    val errorMessage: LiveData<Unit>
        get() = _errorMessage

    private val _errorMessageException = MutableLiveData<String>()
    val errorMessageException: LiveData<String>
        get() = _errorMessageException

    val disposable = CompositeDisposable()

    fun getImageById(id: Int) {
        disposable.add(getImageByIdUseCase(id)
            .doOnSubscribe {
                showProgress()
            }
            .subscribe ({
                when (it) {
                    is Response.Success -> {
                        _image.value = it.data!!
                    }
                    is Response.Empty -> {
                        _errorMessage.value
                        hideProgress()
                    }
                    is Response.Error -> {
                        _errorMessage.value
                        hideProgress()
                    }
                }
            },
                {
                    _errorMessageException.value = it.message
                    hideProgress()
                }
            )
        )
    }
}