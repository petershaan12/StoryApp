package com.petershaan.storyapp.view.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.petershaan.storyapp.data.ResultState
import com.petershaan.storyapp.data.remote.response.LoginResponse
import com.petershaan.storyapp.data.repository.AuthRepository

class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {
    fun login(email: String, password: String): LiveData<ResultState<LoginResponse>> {
        return authRepository.login(email, password)
    }
}