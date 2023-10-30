package com.adush.pexelsapp.domain.model


data class FeatureCollection(
    val id : String,
    val title : String,
    val mediaCount : Int,
    val photosCount : Int,
    val videosCount : Int,
    var active: Boolean = false
)