package com.adush.pexelsapp.data.network

import com.adush.pexelsapp.data.network.configuration.MAPIConfig
import com.adush.pexelsapp.data.network.model.collection.FeatureCollectionResponseDto
import com.adush.pexelsapp.data.network.model.image.ImageItemDto
import com.adush.pexelsapp.data.network.model.image.ImageResponseDto
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET(MAPIConfig.CURATED_IMAGES)
    fun getCuratedImages(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
    ): Single<ImageResponseDto>

    @GET(MAPIConfig.SEARCH_IMAGES)
    fun searchImagesByQuery(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
        @Query("query") query: String
    ): Single<ImageResponseDto>

    @GET(MAPIConfig.FEATURED_COLLECTIONS_IMAGES)
    fun getFeaturedCollections(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): Single<FeatureCollectionResponseDto>

    @GET(MAPIConfig.CERTAIN_IMAGE)
    fun getImageById(
        @Path("id") id: Int
    ): Single<ImageItemDto>
}