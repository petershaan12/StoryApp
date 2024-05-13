package com.petershaan.storyapp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.petershaan.storyapp.data.ResultState
import com.petershaan.storyapp.data.StoryPagingSource
import com.petershaan.storyapp.data.StoryRemoteMediator
import com.petershaan.storyapp.data.pref.UserPreference
import com.petershaan.storyapp.data.remote.database.StoryDao
import com.petershaan.storyapp.data.remote.database.StoryDatabase
import com.petershaan.storyapp.data.remote.database.StoryEntity
import com.petershaan.storyapp.data.remote.response.StoryItem
import com.petershaan.storyapp.data.remote.response.UploadResponse
import com.petershaan.storyapp.data.remote.retrofit.ApiService
import kotlinx.coroutines.flow.first
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class StoryRepository private constructor(
    private val storyDatabase: StoryDatabase,
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {
    fun getAllStory(): LiveData<PagingData<StoryEntity>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
//            pagingSourceFactory = { StoryPagingSource(apiService, userPreference) }
            remoteMediator = StoryRemoteMediator(storyDatabase, userPreference, apiService),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStories()
            }
        ).liveData
    }

    fun getDetailStory(id: String): LiveData<ResultState<StoryItem>> = liveData {
        emit(ResultState.Loading)
        try {
            val token = userPreference.getToken().first()
            val response = apiService.detailStory("Bearer $token", id)
            val result = response.story
            emit(ResultState.Success(result))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message.toString()))
        }
    }

    fun uploadStory(image: File, description: String): LiveData<ResultState<UploadResponse>> = liveData {
        emit(ResultState.Loading)
        try {
            val token = userPreference.getToken().first()
            val requestDescription = description.toRequestBody("text/plain".toMediaType())
            val requestImage = image.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart = MultipartBody.Part.createFormData(
                "photo",
                image.name,
                requestImage
            )
            val response = apiService.uploadStory("Bearer $token", imageMultipart, requestDescription)
            emit(ResultState.Success(response))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message.toString()))
        }
    }

    fun getStoryWithLocation(): LiveData<ResultState<List<StoryItem>>> = liveData {
        emit(ResultState.Loading)
        try {
            val token = userPreference.getToken().first()
            val response = apiService.getAllStory("Bearer $token", location = 1)
            val result = response.listStory
            emit(ResultState.Success(result))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message.toString()))
        }
    }


    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            storyDatabase: StoryDatabase,
            apiService: ApiService,
            userPreference: UserPreference
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(storyDatabase, apiService, userPreference)
            }.also { instance = it }
    }
}