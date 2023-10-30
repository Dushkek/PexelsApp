package com.adush.pexelsapp.domain.model

data class ImageItem(
    val id : Int,
    val url : String,
    val imageSrc : ImageSrc,
    val name : String,
    val height: Int,
    val width: Int
)