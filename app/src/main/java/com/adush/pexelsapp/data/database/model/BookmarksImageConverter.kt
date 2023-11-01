package com.adush.pexelsapp.data.database.model

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


class BookmarksImageConverter {

    @TypeConverter
    fun stringToSrc(value: String): BookmarksImageSrcDbModel {
        val srcType: Type = object : TypeToken<BookmarksImageSrcDbModel?>() {}.type
        return Gson().fromJson(value, srcType)
    }

    @TypeConverter
    fun srcToString(src: BookmarksImageSrcDbModel): String {
        val gson = Gson()
        return gson.toJson(src)
    }

}