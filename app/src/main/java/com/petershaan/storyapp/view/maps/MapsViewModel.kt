package com.petershaan.storyapp.view.maps

import androidx.lifecycle.ViewModel
import com.petershaan.storyapp.data.repository.StoryRepository

class MapsViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    fun getStoryWithLocation() = storyRepository.getStoryWithLocation()
}