package com.adush.pexelsapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.rxjava2.cachedIn
import com.adush.pexelsapp.domain.usecases.GetFeaturedCollectionsUseCase
import com.adush.pexelsapp.domain.usecases.GetImageListBySearchUseCase
import com.adush.pexelsapp.domain.usecases.GetImageListUseCase
import com.adush.pexelsapp.domain.model.FeatureCollection
import com.adush.pexelsapp.domain.model.ImageItem
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val getFeaturedCollectionsUseCase: GetFeaturedCollectionsUseCase,
    private val getImageListUseCase: GetImageListUseCase,
    private val getImageListBySearchUseCase: GetImageListBySearchUseCase
) : ViewModel() {

    private val _featuredCollections = MutableLiveData<List<FeatureCollection>?>()
    val featuredCollections: LiveData<List<FeatureCollection>?>
        get() = _featuredCollections

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String>
        get() = _errorMessage

    val disposable = CompositeDisposable()

    init {
        getFeaturedCollections()
    }

    private fun getFeaturedCollections() {
        disposable.add(
            getFeaturedCollectionsUseCase()
                .subscribe ({ list ->
                    _featuredCollections.value = list
                }, {
                    _errorMessage.value = it.message.toString()
            })
        )
    }


    fun getImageListObservable(): Observable<PagingData<ImageItem>> {
        return getImageListUseCase()
            .cachedIn(viewModelScope)
    }

    fun getImageListBySearch(query: String): Observable<PagingData<ImageItem>> {
        return getImageListBySearchUseCase(query)
            .cachedIn(viewModelScope)
    }
}