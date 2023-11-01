package com.adush.pexelsapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.adush.pexelsapp.data.database.model.BookmarksImageConverter
import com.adush.pexelsapp.data.database.model.BookmarksImageItemDbModel

@Database(entities = [BookmarksImageItemDbModel::class], version = 1, exportSchema = false)
@TypeConverters(BookmarksImageConverter::class)
abstract class BookmarksImagesDatabase : RoomDatabase() {
    companion object {

        private var db: BookmarksImagesDatabase? = null
        private const val DB_NAME = "images.db"
        private val LOCK = Any()

        fun getInstance(context: Context): BookmarksImagesDatabase {
            synchronized(LOCK) {
                db?.let { return it }
                val instance =
                    Room.databaseBuilder(
                        context,
                        BookmarksImagesDatabase::class.java,
                        DB_NAME
                    )
                        .fallbackToDestructiveMigration()
                        .allowMainThreadQueries()
                        .build()
                db = instance
                return instance
            }
        }
    }

    abstract fun imagesDao(): BookmarksImagesDao
}