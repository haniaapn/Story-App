package com.dicoding.storyappdicoding.ui.register

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyappdicoding.data.local.StoryRepository
import com.dicoding.storyappdicoding.data.remote.Result
import com.dicoding.storyappdicoding.data.remote.response.RegisterResponse
import kotlinx.coroutines.launch

class RegisterViewModel(private val repository: StoryRepository): ViewModel() {

    val registerResult = MutableLiveData<Result<RegisterResponse>>()

    fun register(name: String, email: String, password: String) {
        registerResult.value = Result.Loading
        viewModelScope.launch {
            val result = repository.register(name, email, password)
            registerResult.value = result
        }
    }
}