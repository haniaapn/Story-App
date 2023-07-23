package com.dicoding.storyappdicoding.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.storyappdicoding.data.local.StoryRepository
import com.dicoding.storyappdicoding.data.remote.response.ListStoryItem

class MainViewModel(private val storyRepository: StoryRepository): ViewModel() {

    fun stories(): LiveData<PagingData<ListStoryItem>> =
        storyRepository.getStory().cachedIn(viewModelScope)

    fun deleteUser(){
        return storyRepository.deleteUser()
    }
}