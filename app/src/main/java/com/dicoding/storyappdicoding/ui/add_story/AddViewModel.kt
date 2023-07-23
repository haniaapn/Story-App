package com.dicoding.storyappdicoding.ui.add_story

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyappdicoding.data.local.StoryRepository
import com.dicoding.storyappdicoding.data.remote.response.AddNewStoryResponse
import com.dicoding.storyappdicoding.data.remote.Result
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class AddViewModel(private val repository: StoryRepository): ViewModel() {

    val resultAddStory = MutableLiveData<Result<AddNewStoryResponse>>()

    fun addNewStory(description: String, photo: MultipartBody.Part, lat: Float?, lon: Float?) {
        resultAddStory.value = Result.Loading
        viewModelScope.launch {
            val result = repository.addNewStory(description, photo, lat, lon)
            resultAddStory.value = result
        }
    }
}
