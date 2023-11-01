package com.adush.pexelsapp.ui.details

import androidx.lifecycle.*
import com.adush.pexelsapp.domain.Response
import com.adush.pexelsapp.domain.model.ImageItem
import com.adush.pexelsapp.domain.usecases.AddImageToBookmarksUseCase
import com.adush.pexelsapp.domain.usecases.DeleteImageFromBookmarksUseCase
import com.adush.pexelsapp.domain.usecases.GetImageByIdUseCase
import com.adush.pexelsapp.domain.usecases.GetImageFromDbUseCase
import com.adush.pexelsapp.ui.progress.ProgressViewModel
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class DetailsViewModel @Inject constructor(
    private val getImageByIdUseCase: GetImageByIdUseCase,
    private val getImageFromDbUseCase: GetImageFromDbUseCase,
    private val addImageToBookmarksUseCase: AddImageToBookmarksUseCase,
    private val deleteImageFromBookmarksUseCase: DeleteImageFromBookmarksUseCase
): ProgressViewModel(), LifecycleEventObserver {

    private val _image = MutableLiveData<ImageItem>()
    val image: LiveData<ImageItem>
        get() = _image

    private val _imageFromDb = MutableLiveData<ImageItem>()
    val imageFromDb: LiveData<ImageItem>
        get() = _imageFromDb

    private val _errorMessage = MutableLiveData<Unit>()
    val errorMessage: LiveData<Unit>
        get() = _errorMessage

    private val _errorMessageException = MutableLiveData<String>()
    val errorMessageException: LiveData<String>
        get() = _errorMessageException

    private val disposable = CompositeDisposable()

    fun getImageById(id: Int) {
        disposable.add(getImageByIdUseCase(id)
            .doOnSubscribe {
                showProgress()
            }
            .doOnError {
                hideProgress()
            }
            .subscribe ({
                when (it) {
                    is Response.Success -> {
                        _image.value = it.data!!
                    }
                    is Response.Empty -> {
                        hideProgress()
                        _errorMessage.value
                    }
                    is Response.Error -> {
                        hideProgress()
                        _errorMessage.value
                    }
                }
            },
                {
                    _errorMessageException.value = it.message
                }
            )
        )
    }

    fun getImageFromDb(id: Int) {
        disposable.add(getImageFromDbUseCase(id)
            .doOnSubscribe {
                showProgress()
            }
            .subscribe ({
                when (it) {
                    is Response.Success -> {
                        _imageFromDb.value = it.data!!
                        _image.value = it.data!!
                    }
                    is Response.Empty -> {
                        hideProgress()
                        _errorMessage.value
                    }
                    is Response.Error -> {
                        hideProgress()
                        _errorMessage.value
                    }
                }
            },
                {
                    _errorMessageException.value = it.message
                }
            )
        )
    }

    fun addImageToBookmarks(imageItem: ImageItem) {
        addImageToBookmarksUseCase(imageItem)
            .subscribe()
    }

    fun deleteImageFromBookmarks(imageItem: ImageItem) {
        deleteImageFromBookmarksUseCase(imageItem)
            .subscribe()
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when(event){
            Lifecycle.Event.ON_DESTROY -> disposable.clear()
            else -> {}
        }
    }
}