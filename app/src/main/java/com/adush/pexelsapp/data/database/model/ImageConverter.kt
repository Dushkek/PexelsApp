package com.adush.pexelsapp.data.database.model

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


class ImageConverter {

    @TypeConverter
    fun stringToSrc(value: String): ImageSrcDbModel {
        val srcType: Type = object : TypeToken<ImageSrcDbModel?>() {}.type
        return Gson().fromJson(value, srcType)
    }

    @TypeConverter
    fun srcToString(src: ImageSrcDbModel): String {
        val gson = Gson()
        return gson.toJson(src)
    }

}