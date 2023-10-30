package com.adush.pexelsapp.domain

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.adush.pexelsapp.domain.model.FeatureCollection
import com.adush.pexelsapp.domain.model.ImageItem
import io.reactivex.Observable

interface ImageRepository {

    fun getFeaturedCollections(): Observable<List<FeatureCollection>>

    fun getImageList(): Observable<PagingData<ImageItem>>

    fun getImageListBySearch(query: String): Observable<PagingData<ImageItem>>

    fun getImageItem(id: Int): LiveData<ImageItem>

    fun addImageItem(imageItem: ImageItem)
}