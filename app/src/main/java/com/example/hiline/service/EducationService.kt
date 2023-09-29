package com.example.hiline.service

import com.example.hiline.request.LinkRequest
import com.example.hiline.request.MoFRequest
import com.example.hiline.response.AnswerResponse
import com.example.hiline.response.EducationResponse
import com.example.hiline.response.EducationsResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface EducationService {
    @GET("/api/educations")
    fun getEducations(
        @Query("page") page: Int,
        @Header("Authorization") refreshToken: String?
    ): Call<EducationsResponse>

    @GET("/api/educations")
    fun getEducationsFilter(
        @Query("page") page: Int,
        @Query("category") category: Int,
        @Header("Authorization") refreshToken: String?
    ): Call<EducationsResponse>

    @GET("/api/education/{id}")
    fun getEducation(
        @Path("id") id: String,
        @Header("Authorization") refreshToken: String?
    ): Call<EducationResponse>

    @POST("/api/education/answer")
    fun answerLink(
        @Body request: LinkRequest,
        @Header("Authorization") refreshToken: String?
    ): Call<AnswerResponse>

    @POST("/api/education/answer")
    fun answerMoF(
        @Body request: MoFRequest,
        @Header("Authorization") refreshToken: String?
    ): Call<AnswerResponse>

}