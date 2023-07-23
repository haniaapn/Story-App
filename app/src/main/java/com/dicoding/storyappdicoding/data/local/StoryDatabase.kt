package com.dicoding.storyappdicoding.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dicoding.storyappdicoding.data.local.database.RemoteKeys
import com.dicoding.storyappdicoding.data.local.database.RemoteKeysDao
import com.dicoding.storyappdicoding.data.remote.response.ListStoryItem

@Database(
    entities =[ListStoryItem::class, RemoteKeys::class],
    version = 1,
    exportSchema = false
)
abstract class StoryDatabase: RoomDatabase() {
    abstract fun getStoryDao(): StoryDao
    abstract fun getRemoteKeysDao(): RemoteKeysDao

    companion object{
        @Volatile
        private var INSTANCE : StoryDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): StoryDatabase{
            return INSTANCE ?: synchronized(this){
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    StoryDatabase::class.java,"story_database"
                ).fallbackToDestructiveMigration().build().also { INSTANCE = it }
            }
        }
    }

}