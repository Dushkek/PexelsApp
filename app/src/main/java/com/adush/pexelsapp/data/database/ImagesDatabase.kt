package com.adush.pexelsapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.adush.pexelsapp.data.database.model.ImageConverter
import com.adush.pexelsapp.data.database.model.ImageItemDbModel

@Database(entities = [ImageItemDbModel::class], version = 1, exportSchema = false)
@TypeConverters(ImageConverter::class)
abstract class ImagesDatabase : RoomDatabase() {
    companion object {

        private var db: ImagesDatabase? = null
        private const val DB_NAME = "images.db"
        private val LOCK = Any()

        fun getInstance(context: Context): ImagesDatabase {
            synchronized(LOCK) {
                db?.let { return it }
                val instance =
                    Room.databaseBuilder(
                        context,
                        ImagesDatabase::class.java,
                        DB_NAME
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                db = instance
                return instance
            }
        }
    }

    abstract fun imagesDao(): ImagesDao
}