package com.adush.pexelsapp.data.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "images_list")
data class ImageItemDbModel(
    @PrimaryKey
    val id : Int,
    val url : String,
    val imageSrc : ImageSrcDbModel,
    val name : String,
    val bookmark: Boolean
)