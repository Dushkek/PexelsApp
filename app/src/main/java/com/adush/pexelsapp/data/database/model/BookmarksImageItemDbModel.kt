package com.adush.pexelsapp.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "images_list")
data class BookmarksImageItemDbModel(
    @PrimaryKey
    val id : Int,
    @ColumnInfo(name = "url")
    val url : String,
    @ColumnInfo(name = "author")
    val author : String,
    @ColumnInfo(name = "image_Src")
    val imageSrc : BookmarksImageSrcDbModel,
    @ColumnInfo(name = "name")
    val name : String,
    @ColumnInfo(name = "height")
    val height: Int,
    @ColumnInfo(name = "width")
    val width: Int,
    @ColumnInfo(name = "bookmark")
    val bookmark: Boolean = false
)