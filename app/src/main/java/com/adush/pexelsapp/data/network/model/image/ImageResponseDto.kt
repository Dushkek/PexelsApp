package com.adush.pexelsapp.data.network.model.image

import com.google.gson.annotations.SerializedName

data class ImageResponseDto(
    @SerializedName("page")
    val page : Int,
    @SerializedName("per_page")
    val perPage : Int,
    @SerializedName("photos")
    val images: List<ImageItemDto> ?= null,
    @SerializedName("total_results")
    val totalResults : Int
)
