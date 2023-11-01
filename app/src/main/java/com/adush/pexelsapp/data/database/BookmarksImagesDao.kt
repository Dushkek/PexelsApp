package com.adush.pexelsapp.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.adush.pexelsapp.data.database.model.BookmarksImageItemDbModel
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

@Dao
interface BookmarksImagesDao {

    @Query("SELECT * FROM images_list ORDER BY id ASC LIMIT :limit OFFSET :offset")
    fun getImagesList(limit: Int, offset: Int): Single<List<BookmarksImageItemDbModel>>

    @Query("SELECT * FROM images_list WHERE id == :id LIMIT 1")
    fun getImage(id: Int): Single<BookmarksImageItemDbModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertImage(image: BookmarksImageItemDbModel): Completable

    @Delete
    fun deleteImage(image: BookmarksImageItemDbModel): Completable
}