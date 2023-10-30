package com.adush.pexelsapp.data.network.model.image

import com.google.gson.annotations.SerializedName

data class ImageItemDto(
    @SerializedName("id")
    val id : Int,
    @SerializedName("url")
    val url : String,
    @SerializedName("src")
    val imageSrc : ImageSrcDto,
    @SerializedName("alt")
    val name : String,
    @SerializedName("height")
    val height: Int,
    @SerializedName("width")
    val width: Int
)
