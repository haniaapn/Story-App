package com.dicoding.storyappdicoding.ui.detail_story

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyappdicoding.data.local.StoryRepository
import kotlinx.coroutines.launch
import com.dicoding.storyappdicoding.data.remote.Result
import com.dicoding.storyappdicoding.data.remote.response.DetailStoryResponse

class DetailViewModel(private val repository: StoryRepository): ViewModel() {

    val detailStoryResult = MutableLiveData<Result<DetailStoryResponse>>()

    fun getDetailStory(storyId: String) {
        detailStoryResult.value = Result.Loading
        viewModelScope.launch {
            val result = repository.getDetailStory(storyId)
            detailStoryResult.value = result
        }
    }
}