package com.dicoding.storyappdicoding.ui.splash

import androidx.lifecycle.ViewModel
import com.dicoding.storyappdicoding.data.local.StoryRepository

class SplashViewModel (private val repository: StoryRepository): ViewModel(){

    fun getUser(): Boolean {
        return repository.getUser().isLogin
    }
}