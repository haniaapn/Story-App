package com.dicoding.storyappdicoding.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyappdicoding.data.remote.Result
import com.dicoding.storyappdicoding.data.local.StoryRepository
import com.dicoding.storyappdicoding.data.remote.response.LoginResponse
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: StoryRepository) : ViewModel() {

    val loginResult = MutableLiveData<Result<LoginResponse>>()

    fun login(email: String, password: String) {
        loginResult.value = Result.Loading
        viewModelScope.launch {
            val result = repository.login(email, password)
            loginResult.value = result
        }
    }
}