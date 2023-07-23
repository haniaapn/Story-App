package com.dicoding.storyappdicoding.ui.map

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyappdicoding.data.local.StoryRepository
import com.dicoding.storyappdicoding.data.remote.Result
import com.dicoding.storyappdicoding.data.remote.response.ListStoryItem
import kotlinx.coroutines.launch

class MapsViewModel(private val repository: StoryRepository) : ViewModel() {

    val locationResult = MutableLiveData<Result<List<ListStoryItem>>>()



    fun getLocation() {
        locationResult.value = Result.Loading
        viewModelScope.launch {
        val result = repository.getLocation()
            locationResult.value = result
        }
    }



}
