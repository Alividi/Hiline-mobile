package com.example.hiline.api

import com.example.hiline.model.CommentRequest
import com.example.hiline.model.CommentResponse
import com.example.hiline.model.ForumRequest
import com.example.hiline.model.ForumResponse
import com.example.hiline.model.ForumsResponse
import com.example.hiline.model.ReportRequest
import com.example.hiline.model.ReportResponse
import com.example.hiline.model.ReportsResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ForumApi {
    @GET("forums")
    fun getForums(
        @Header("Authorization") token_auth: String?
    ): Call<ForumsResponse>

    @GET("forums?filter=new")
    fun getForumsNew(
        @Header("Authorization") token_auth: String?
    ): Call<ForumsResponse>

    @GET("forums?filter=popular")
    fun getForumsPopular(
        @Header("Authorization") token_auth: String?
    ): Call<ForumsResponse>

    @GET("forums?filter=favorite")
    fun getForumsFavorite(
        @Header("Authorization") token_auth: String?
    ): Call<ForumsResponse>

    @GET("forum/{id}")
    fun getForum(
        @Path("id") id: String,
        @Header("Authorization") token_auth: String?
    ): Call<ForumResponse>

    @POST("forum")
    fun postForum(
        @Header("Authorization") token_auth: String?,
        @Body forumRequest: ForumRequest
    ): Call<ForumResponse>

    @PUT("forum/{id}")
    fun putForum(
        @Path("id") id: String,
        @Header("Authorization") token_auth: String?,
        @Body forumRequest: ForumRequest
    ): Call<ForumResponse>

    @DELETE("forum/{id}")
    fun deleteForum(
        @Path("id") id: String,
        @Header("Authorization") token_auth: String?
    ): Call<ForumResponse>

    @POST("favorite/{id}")
    fun favForum(
        @Path("id") id: String,
        @Header("Authorization") token_auth: String?
    ): Call<ForumsResponse>

    @POST("comment")
    fun postComment(
        @Header("Authorization") token_auth: String?,
        @Body commentRequest: CommentRequest
    ): Call<CommentResponse>

    @DELETE("comment/{id}")
    fun deleteComment(
        @Path("id") id: String,
        @Header("Authorization") token_auth: String?
    ): Call<CommentResponse>

    @GET("comment/{id}")
    fun getComment(
        @Path("id") id: String,
        @Header("Authorization") token_auth: String?
    ): Call<CommentResponse>

    @POST("comment/like/{id}")
    fun likeComment(
        @Path("id") id: String,
        @Header("Authorization") token_auth: String?
    ): Call<CommentResponse>

    @GET("reports")
    fun getReports(
        @Header("Authorization") token_auth: String?
    ): Call<ReportsResponse>

    @GET("report/{id}")
    fun getReport(
        @Path("id") id: String,
        @Header("Authorization") token_auth: String?
    ): Call<ReportResponse>

    @POST("report")
    fun postReport(
        @Header("Authorization") token_auth: String?,
        @Body reportRequest: ReportRequest
    ): Call<ReportResponse>

    @DELETE("report/{id}")
    fun deleteReport(
        @Path("id") id: String,
        @Header("Authorization") token_auth: String?
    ): Call<ReportResponse>
}