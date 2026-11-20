package com.example.gallery.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.gallery.domain.model.Album
import com.example.gallery.domain.model.Face
import com.example.gallery.domain.model.FaceGroup
import com.example.gallery.domain.model.MediaItem
import com.example.gallery.domain.model.SearchHistory

@Database(
    entities = [
        MediaItem::class,
        FaceGroup::class,
        Face::class,
        Album::class,
        SearchHistory::class,
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class MediaDatabase : RoomDatabase() {
    abstract fun mediaDao(): MediaDao

    companion object {
        @Volatile
        private var INSTANCE: MediaDatabase? = null

        fun getInstance(context: Context): MediaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MediaDatabase::class.java,
                    "gallery.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}