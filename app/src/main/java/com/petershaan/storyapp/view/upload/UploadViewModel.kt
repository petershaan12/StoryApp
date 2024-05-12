package com.petershaan.storyapp.view.upload

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.petershaan.storyapp.data.ResultState
import com.petershaan.storyapp.data.remote.response.UploadResponse
import com.petershaan.storyapp.data.repository.StoryRepository
import java.io.File

class UploadViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    fun uploadImage(image: File, description: String): LiveData<ResultState<UploadResponse>> {
        return storyRepository.uploadStory(image, description)
    }
}