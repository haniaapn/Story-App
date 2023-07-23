package com.dicoding.storyappdicoding.data.remote.retrofit

import com.dicoding.storyappdicoding.data.remote.response.*
import okhttp3.MultipartBody
import retrofit2.http.*

interface ApiService {
    // Register
    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

    // Login
    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    // Add New Story
    @Multipart
    @POST("stories")
    suspend fun addNewStory(
        @Header("Authorization") token: String,
        @Part("description") description: String,
        @Part photo: MultipartBody.Part,
        @Part("lat") lat: Float?,
        @Part("lon") lon: Float?
    ): AddNewStoryResponse

    // Get All Stories
    @GET("stories")
    suspend fun getAllStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int?,
        @Query("size") size: Int?,
        @Query("location") location: Int?
    ): StoryResponse

    @GET("stories")
    suspend fun getAllStoriesWithLocation(
        @Header("Authorization") token: String,
        @Query("location") location: Int = 1
    ): StoryResponse


    // Detail Story
    @GET("stories/{id}")
    suspend fun getDetailStory(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): DetailStoryResponse
}