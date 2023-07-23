package com.dicoding.storyappdicoding.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dicoding.storyappdicoding.data.remote.response.ListStoryItem

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(story: List<ListStoryItem>)

    @Query("SELECT * FROM stories ORDER BY createdAt DESC")
    fun getAllStories(): PagingSource<Int, ListStoryItem>

    @Query("DELETE FROM stories")
    suspend fun deleteAll()
}