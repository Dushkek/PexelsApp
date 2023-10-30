package com.adush.pexelsapp.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.adush.pexelsapp.data.database.model.ImageItemDbModel

@Dao
interface ImagesDao {

    @Query("SELECT * FROM images_list")
    fun getImagesList(): LiveData<List<ImageItemDbModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertImagesList(imagesList: List<ImageItemDbModel>)

    @Query("DELETE FROM images_list")
    fun deleteAll()
}