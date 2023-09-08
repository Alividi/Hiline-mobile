package com.example.hiline.api

import com.example.hiline.model.RefreshTokenResponse
import retrofit2.Call
import retrofit2.http.POST

interface AuthService {
    @POST("/api/auth/refresh")
    fun refreshToken(): Call<RefreshTokenResponse>
}
