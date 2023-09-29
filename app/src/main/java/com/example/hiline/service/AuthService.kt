package com.example.hiline.service


import com.example.hiline.request.RegisterRequest
import com.example.hiline.response.CurrentResponse
import com.example.hiline.response.RefreshTokenResponse
import com.example.hiline.response.UserResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthService {
    @POST("/api/auth/refresh")
    fun refreshToken(
        @Header("Authorization") refreshToken: String?
    ): Call<RefreshTokenResponse>

    @GET("/api/auth/current-user")
    fun currentUser(
        @Header("Authorization") accessToken: String?
    ): Call<CurrentResponse>

    @POST("/api/auth/logout")
    fun logout(
        @Header("Authorization") accessToken: String?
    ): Call<CurrentResponse>
}