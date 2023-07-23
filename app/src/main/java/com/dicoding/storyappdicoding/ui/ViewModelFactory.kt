package com.dicoding.storyappdicoding.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storyappdicoding.data.local.StoryRepository
import com.dicoding.storyappdicoding.di.Injection
import com.dicoding.storyappdicoding.ui.add_story.AddViewModel
import com.dicoding.storyappdicoding.ui.detail_story.DetailViewModel
import com.dicoding.storyappdicoding.ui.home.MainViewModel
import com.dicoding.storyappdicoding.ui.login.LoginViewModel
import com.dicoding.storyappdicoding.ui.map.MapsViewModel
import com.dicoding.storyappdicoding.ui.register.RegisterViewModel
import com.dicoding.storyappdicoding.ui.splash.SplashViewModel

class ViewModelFactory(
    private val storyRepository: StoryRepository
    ): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(SplashViewModel::class.java) -> {
                SplashViewModel(storyRepository) as T
            }
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(storyRepository) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(storyRepository) as T
            }
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(storyRepository) as T
            }
            modelClass.isAssignableFrom(DetailViewModel::class.java) -> {
                DetailViewModel(storyRepository) as T
            }
            modelClass.isAssignableFrom(AddViewModel::class.java) -> {
                AddViewModel(storyRepository) as T
            }
            modelClass.isAssignableFrom(MapsViewModel::class.java) -> {
                MapsViewModel(storyRepository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getModelFactory(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(Injection.provideRepository(context))
            }.also { instance = it }
    }
}