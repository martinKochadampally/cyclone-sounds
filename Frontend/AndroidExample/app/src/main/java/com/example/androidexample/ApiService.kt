package com.example.androidexample

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST

data class UserInfo(
    val name: String,
    val email: String,
    val joinDate: String
)

data class SignUpResponse(
    val status: String,
    val userId: String
)

data class LoginResponse(
    val token: String,
    val userId: String,
    val status: String
)

interface ApiService {
    @GET("users/12345")
    fun getUserInfo(): Call<UserInfo>

    @POST("signup")
    fun signUpUser(): Call<SignUpResponse>

    @POST("login")
    fun loginUser(): Call<LoginResponse>
}