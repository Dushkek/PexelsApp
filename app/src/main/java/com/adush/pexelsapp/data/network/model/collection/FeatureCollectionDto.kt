package com.adush.pexelsapp.data.network.model.collection

import com.google.gson.annotations.SerializedName

data class FeatureCollectionDto(
    @SerializedName("id")
    val id : String,
    @SerializedName("title")
    val title : String,
    @SerializedName("media_count")
    val mediaCount : Int,
    @SerializedName("photos_count")
    val photosCount : Int,
    @SerializedName("videos_count")
    val videosCount : Int
)