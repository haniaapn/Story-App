package com.dicoding.storyappdicoding.di

import android.content.Context
import com.dicoding.storyappdicoding.data.local.StoryDatabase
import com.dicoding.storyappdicoding.data.local.StoryRepository
import com.dicoding.storyappdicoding.data.remote.retrofit.ApiConfig
import com.dicoding.storyappdicoding.model.UserPreference

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val apiService = ApiConfig.getApiService()
        val userPreference = UserPreference.getUserPreference(context)
        return StoryRepository.getStoryRepository(apiService, userPreference, StoryDatabase.getDatabase(context))
    }
}