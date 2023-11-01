package com.adush.pexelsapp.domain

import androidx.paging.PagingData
import com.adush.pexelsapp.domain.model.FeatureCollection
import com.adush.pexelsapp.domain.model.ImageItem
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

interface ImageRepository {

    fun getFeaturedCollections(): Observable<List<FeatureCollection>>

    fun getImageList(): Observable<PagingData<ImageItem>>

    fun getImageListBySearch(query: String): Observable<PagingData<ImageItem>>

    fun getImageById(id: Int): Observable<Response<ImageItem>>

    fun getImageListFromDb(): Observable<PagingData<ImageItem>>

    fun getImageFromDb(id: Int): Single<Response<ImageItem>>

    fun addImageItemToDb(imageItem: ImageItem): Completable

    fun deleteImageItemFromDb(imageItem: ImageItem): Completable
}