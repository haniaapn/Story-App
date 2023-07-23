package com.dicoding.storyappdicoding.data.local

import androidx.lifecycle.LiveData
import androidx.paging.*
import com.dicoding.storyappdicoding.StoryRemoteMediator
import com.dicoding.storyappdicoding.data.remote.retrofit.ApiService
import com.dicoding.storyappdicoding.data.remote.Result
import com.dicoding.storyappdicoding.data.remote.body.LoginRequest
import com.dicoding.storyappdicoding.data.remote.body.RegisterRequest
import com.dicoding.storyappdicoding.data.remote.response.*
import com.dicoding.storyappdicoding.model.UserModel
import com.dicoding.storyappdicoding.model.UserPreference
import okhttp3.MultipartBody

class StoryRepository(
    private val apiService: ApiService,
    private val userPreference: UserPreference,
    private val storyDatabase: StoryDatabase
    ) {

    suspend fun login(email: String, password: String): Result<LoginResponse> {
        return try {
            val loginRequest = LoginRequest(email,password)
            val response = apiService.login(loginRequest.email,loginRequest.password)
            if (response.error) {
                Result.Error(response.message)
            } else {
                Result.Success(response)
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "An error occurred")
        }
    }

    suspend fun register(name: String, email: String, password: String): Result<RegisterResponse> {
        return try {
            val registerRequest = RegisterRequest(name, email, password)
            val response = apiService.register(registerRequest.name, registerRequest.email, registerRequest.password)
            if (response.error) {
                Result.Error(response.message)
            } else {
                Result.Success(response)
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "An error occurred")
        }
    }

    suspend fun addNewStory(description: String, photo: MultipartBody.Part, lat: Float?, lon: Float?): Result<AddNewStoryResponse> {
        return try {
            val token = "Bearer ${userPreference.getUser().token}"
            val response = apiService.addNewStory(token, description, photo, lat, lon)
            if (response.error) {
                Result.Error(response.message)
            } else {
                Result.Success(response)
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "An error occurred")
        }
    }

    suspend fun getDetailStory(id: String): Result<DetailStoryResponse> {
        return try {
            val token = "Bearer ${userPreference.getUser().token}"
            val response = apiService.getDetailStory(token, id)
            if (response.error) {
                Result.Error(response.message)
            } else {
                Result.Success(response)
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "An error occurred")
        }
    }


    suspend fun getLocation(): Result<List<ListStoryItem>> {
        return try {
            val token = "Bearer ${userPreference.getUser().token}"
            val response = apiService.getAllStoriesWithLocation(token, 1)
            if (response.listStory.isNotEmpty()) {
                Result.Success(response.listStory)
            } else {
                Result.Error("No story location items available")
            }
        } catch (e: Exception) {
            Result.Error("Error: ${e.message}")
        }
    }

    fun getStory(): LiveData<PagingData<ListStoryItem>>{
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(5),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService, userPreference),
            pagingSourceFactory ={
                storyDatabase.getStoryDao().getAllStories()
            }
        ).liveData
    }

    fun getUser(): UserModel {
        return userPreference.getUser()
    }

    fun deleteUser(){
        return userPreference.deleteUser()
    }

    companion object {

        @Volatile
        private var instance: StoryRepository? = null
        fun getStoryRepository(
            apiService: ApiService,
            preference: UserPreference,
            storyDatabase: StoryDatabase
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService,preference, storyDatabase)
            }.also { instance = it }
    }
}