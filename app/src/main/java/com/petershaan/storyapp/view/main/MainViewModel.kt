package com.petershaan.storyapp.view.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.petershaan.storyapp.data.pref.UserModel
import com.petershaan.storyapp.data.remote.response.StoryItem
import com.petershaan.storyapp.data.repository.AuthRepository
import com.petershaan.storyapp.data.repository.StoryRepository
import kotlinx.coroutines.launch


class MainViewModel(storyRepository: StoryRepository, private val repository: AuthRepository) : ViewModel() {

    private val refresh = MutableLiveData<Unit>()
    init {
        refreshData()
    }

    val story: LiveData<PagingData<StoryItem>> = refresh.switchMap {
        storyRepository.getAllStory().cachedIn(viewModelScope)
    }

    fun refreshData() {
        refresh.value = Unit
    }

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}