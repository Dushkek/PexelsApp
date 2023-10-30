package com.adush.pexelsapp.data.network.model.collection

import com.google.gson.annotations.SerializedName

data class FeatureCollectionResponseDto(
    @SerializedName("collections")
    val collection : List<FeatureCollectionDto> ?= null,
    @SerializedName("page")
    val page : Int,
    @SerializedName("per_page")
    val perPage : Int
)
