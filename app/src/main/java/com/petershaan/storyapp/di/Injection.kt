package com.petershaan.storyapp.di

import android.content.Context
import com.petershaan.storyapp.data.pref.UserPreference
import com.petershaan.storyapp.data.pref.dataStore
import com.petershaan.storyapp.data.remote.database.StoryDatabase
import com.petershaan.storyapp.data.remote.retrofit.ApiConfig
import com.petershaan.storyapp.data.repository.AuthRepository
import com.petershaan.storyapp.data.repository.StoryRepository
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideAuthRepository(context: Context): AuthRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        return AuthRepository.getInstance(apiService, pref)
    }

    fun provideStoryRepository(context: Context): StoryRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val database = StoryDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService()
        return StoryRepository.getInstance(database, apiService, pref)
    }
}