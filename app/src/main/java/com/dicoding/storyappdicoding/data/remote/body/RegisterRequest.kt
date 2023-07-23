package com.dicoding.storyappdicoding.data.remote.body

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)
