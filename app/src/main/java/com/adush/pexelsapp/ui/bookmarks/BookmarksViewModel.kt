package com.adush.pexelsapp.ui.bookmarks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.rxjava2.cachedIn
import com.adush.pexelsapp.domain.model.ImageItem
import com.adush.pexelsapp.domain.usecases.GetImageListFromDbUseCase
import io.reactivex.Observable
import javax.inject.Inject

class BookmarksViewModel @Inject constructor(
    private val getImageListFromDbUseCase: GetImageListFromDbUseCase
): ViewModel() {

    fun getImageListFromDb(): Observable<PagingData<ImageItem>> {
        return getImageListFromDbUseCase()
            .cachedIn(viewModelScope)
    }
}