package com.example.hiline.api

import com.example.hiline.model.EditProfileAdminRequest
import com.example.hiline.model.EditProfileRequest
import com.example.hiline.model.LoginRequest
import com.example.hiline.model.ProfileResponse
import com.example.hiline.model.RegisterRequest
import com.example.hiline.model.ResetPWRequest
import com.example.hiline.model.UserResponse
import com.example.hiline.model.ValidateResetPWRequest
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part

interface UserApi {

    @POST("/api/auth/login")
    fun login(
        @Body loginRequest: LoginRequest
    ): Call<UserResponse>

    @POST("/api/user")
    fun signup(
        @Body registerRequest: RegisterRequest
    ): Call<UserResponse>

    //before
    @POST("signin")
    fun signin(
        @Body loginRequest: LoginRequest
    ): Call<UserResponse>

    @PUT("profile")
    fun editProfile(
        @Header("Authorization") token_auth: String?,
        @Body editProfileRequest: EditProfileRequest
    ): Call<ProfileResponse>

    @PUT("profile")
    fun editProfileAdmin(
        @Header("Authorization") token_auth: String?,
        @Body editProfileAdminRequest: EditProfileAdminRequest
    ): Call<ProfileResponse>

    @Multipart
    @POST("image-profile")
    fun uploadImage(
        @Header("Authorization") token_auth: String?,
        @Part image: MultipartBody.Part
    ): Call<ProfileResponse>

    @POST("validate/reset-password")
    fun validateResetPW(
        @Header("Authorization") token_auth: String?,
        @Body validateResetPWRequest: ValidateResetPWRequest
    ): Call<ProfileResponse>

    @POST("reset-password")
    fun resetPW(
        @Header("Authorization") token_auth: String?,
        @Body resetPWRequest: ResetPWRequest
    ): Call<ProfileResponse>
}