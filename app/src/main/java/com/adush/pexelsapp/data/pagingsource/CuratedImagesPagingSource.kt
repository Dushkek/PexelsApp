package com.adush.pexelsapp.data.pagingsource

import androidx.paging.*
import androidx.paging.rxjava2.RxPagingSource
import com.adush.pexelsapp.data.mapper.ImageMapper
import com.adush.pexelsapp.data.network.ApiService
import com.adush.pexelsapp.data.network.configuration.MAPIConfig
import com.adush.pexelsapp.domain.model.ImageItem
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class CuratedImagesPagingSource @Inject constructor(
    private val apiService: ApiService,
    private val mapper: ImageMapper
) : RxPagingSource<Int, ImageItem>() {

    override fun loadSingle(params: LoadParams<Int>): Single<LoadResult<Int, ImageItem>> {
        val page = params.key ?: MAPIConfig.STARTING_INDEX_PAGE
        val response = apiService.getCuratedImages(page, params.loadSize)

        return response
            .subscribeOn(Schedulers.io())
            .map { response ->
                response.images?.map { mapper.mapImageDtoToEntity(it) }
            }
            .map {
                toLoadResult(it, page, params.loadSize)
            }
            .onErrorReturn { e ->
                when (e) {
                    is IOException -> LoadResult.Error(e)
                    is HttpException -> LoadResult.Error(e)
                    else -> LoadResult.Error(e)
                } }
    }

    override fun getRefreshKey(state: PagingState<Int, ImageItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    private fun toLoadResult(
        list: List<ImageItem>,
        position: Int,
        paramsSize: Int
    ): LoadResult<Int, ImageItem> {
        return LoadResult.Page(
            data = list,
            prevKey = if (position == MAPIConfig.STARTING_INDEX_PAGE) null else position - 1,
            nextKey = if (list.size != paramsSize) null else position + 1
        )
    }
}