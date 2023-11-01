package com.adush.pexelsapp.data.repository

import android.annotation.SuppressLint
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.rxjava2.observable
import com.adush.pexelsapp.data.database.BookmarksImagesDao
import com.adush.pexelsapp.data.mapper.ImageMapper
import com.adush.pexelsapp.data.network.ApiService
import com.adush.pexelsapp.data.network.configuration.MAPIConfig
import com.adush.pexelsapp.data.pagingsource.BookmarksImagesPagingSource
import com.adush.pexelsapp.data.pagingsource.CuratedImagesPagingSource
import com.adush.pexelsapp.data.pagingsource.SearchImagesPagingSource
import com.adush.pexelsapp.domain.ImageRepository
import com.adush.pexelsapp.domain.Response
import com.adush.pexelsapp.domain.model.FeatureCollection
import com.adush.pexelsapp.domain.model.ImageItem
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.SingleSubject
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class ImageRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val mapper: ImageMapper,
    private val bookmarksImagesDao: BookmarksImagesDao,
    private val curatedImagesPagingSource: CuratedImagesPagingSource,
    private val bookmarksImagesPagingSource: BookmarksImagesPagingSource
) : ImageRepository {

    @SuppressLint("CheckResult")
    override fun getFeaturedCollections(): Observable<List<FeatureCollection>> {
        val getFeaturedCollections = apiService.getFeaturedCollections(
            MAPIConfig.STARTING_INDEX_PAGE,
            MAPIConfig.PER_PAGE_COLLECTIONS_SIZE
        )
        val result = SingleSubject.create<List<FeatureCollection>>()

        getFeaturedCollections
            .subscribeOn(Schedulers.io())
            .map { response ->
                response.collection?.map { mapper.mapCollectionDtoToEntity(it) }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ list ->
                if (!list.isNullOrEmpty()) {
                    result.onSuccess(list)
                }
            }, { e ->
                when (e) {
                    is IOException -> result.onError(e)
                    is HttpException -> result.onError(e)
                    else -> result.onError(e)
                }
            })

        return result.toObservable()
    }

    override fun getImageList(): Observable<PagingData<ImageItem>> {
        return Pager(
            config = setupPagingConfig(),
            pagingSourceFactory = { curatedImagesPagingSource }
        ).observable
    }

    override fun getImageListBySearch(query: String): Observable<PagingData<ImageItem>> {
        return Pager(
            config = setupPagingConfig(),
            pagingSourceFactory = {
                SearchImagesPagingSource(
                    apiService,
                    mapper,
                    query
                )
            }
        ).observable
    }

    @SuppressLint("CheckResult")
    override fun getImageById(id: Int): Observable<Response<ImageItem>> {
        val getImageById = apiService.getImageById(id)

        val result = SingleSubject.create<Response<ImageItem>>()

        getImageById
            .subscribeOn(Schedulers.io())
            .map {
                mapper.mapImageDtoToEntity(it)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({ image ->
                if (image != null) result.onSuccess(Response.Success(image)) else result.onSuccess(Response.Empty)
            },
                { e ->
                    when (e) {
                        is IOException -> result.onError(e)
                        is HttpException -> result.onSuccess(Response.Error(e.message.toString()))
                        else -> result.onError(e)
                    }
                }
            )

        return result.toObservable()
    }

    @SuppressLint("CheckResult")
    override fun getImageListFromDb(): Observable<PagingData<ImageItem>> {
       return Pager(
           config = setupPagingConfig(),
           pagingSourceFactory = { bookmarksImagesPagingSource }
       ).observable
    }

    @SuppressLint("CheckResult")
    override fun getImageFromDb(id: Int): Single<Response<ImageItem>> {
        val getImageFromDb = bookmarksImagesDao.getImage(id)

        val result = SingleSubject.create<Response<ImageItem>>()

        getImageFromDb
            .subscribeOn(Schedulers.io())
            .map {
            mapper.mapBookmarkImageDbToEntity(it)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({ item ->
                if (item != null) result.onSuccess(Response.Success(item)) else result.onSuccess(Response.Empty)
            },
                { e ->
                    when (e) {
                        is IOException -> result.onSuccess(Response.Error(e.message.toString()))
                        is HttpException -> result.onError(e)
                        else -> result.onSuccess(Response.Error(e.message.toString()))
                    }
                }
            )

        return result
    }

    override fun addImageItemToDb(imageItem: ImageItem): Completable {
        val item = mapper.mapEntityToBookmarkImageDb(imageItem)
        val addImageItemToDb = bookmarksImagesDao.insertImage(item)

        return addImageItemToDb
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun deleteImageItemFromDb(imageItem: ImageItem): Completable {
        val item = mapper.mapEntityToBookmarkImageDb(imageItem)
        val deleteImageItemFromDb = bookmarksImagesDao.deleteImage(item)

        return deleteImageItemFromDb
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    private fun setupPagingConfig(): PagingConfig = PagingConfig(
        pageSize = MAPIConfig.PER_PAGE_SIZE,
        initialLoadSize = MAPIConfig.PER_PAGE_SIZE,
        enablePlaceholders = true,
        prefetchDistance = MAPIConfig.PREFETCH_DISTANCE
    )
}