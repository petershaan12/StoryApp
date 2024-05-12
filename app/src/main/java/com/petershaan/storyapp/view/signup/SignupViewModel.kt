package com.petershaan.storyapp.view.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.petershaan.storyapp.data.ResultState
import com.petershaan.storyapp.data.remote.response.RegisterResponse
import com.petershaan.storyapp.data.repository.AuthRepository

class SignupViewModel(private val authRepository: AuthRepository) : ViewModel() {
    fun register(name: String, email: String, password: String): LiveData<ResultState<RegisterResponse>> {
        return authRepository.register(name, email, password)
    }
}