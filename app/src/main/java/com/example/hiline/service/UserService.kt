package com.example.hiline.service

import com.example.hiline.request.EditProfileAdminRequest
import com.example.hiline.request.EditProfileRequest
import com.example.hiline.request.LoginRequest
import com.example.hiline.request.LupaPwRequest
import com.example.hiline.response.ProfileResponse
import com.example.hiline.request.RegisterRequest
import com.example.hiline.request.ResetPWRequest
import com.example.hiline.response.UserResponse
import com.example.hiline.request.ValidateResetPWRequest
import com.example.hiline.response.GantiPwResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part

interface UserService {
    @POST("/api/auth/login")
    fun login(
        @Body loginRequest: LoginRequest
    ): Call<UserResponse>

    @POST("/api/user")
    fun signup(
        @Body registerRequest: RegisterRequest
    ): Call<UserResponse>

    @POST("/request-reset-password")
    fun lupaPassword(
        @Body lupaPwRequest: LupaPwRequest
    ): Call<UserResponse>

    @POST("/request-change-password")
    fun validateResetPW(
        @Header("Authorization") aToken: String?,
        @Body validateResetPWRequest: ValidateResetPWRequest
    ): Call<GantiPwResponse>

    @POST("/change-password")
    fun resetPW(
        @Header("Authorization") aToken: String?,
        @Body resetPWRequest: ResetPWRequest
    ): Call<GantiPwResponse>

    @PUT("/api/user")
    fun editProfile(
        @Header("Authorization") aToken: String?,
        @Body editProfileRequest: EditProfileRequest
    ): Call<ProfileResponse>

    @PUT("/api/user")
    fun editProfileAdmin(
        @Header("Authorization") aToken: String?,
        @Body editProfileAdminRequest: EditProfileAdminRequest
    ): Call<ProfileResponse>
}