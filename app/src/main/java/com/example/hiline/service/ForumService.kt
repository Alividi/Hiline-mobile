package com.example.hiline.service

import com.example.hiline.request.CommentRequest
import com.example.hiline.request.ForumRequest
import com.example.hiline.response.ForumResponse
import com.example.hiline.response.ForumsResponse
import com.example.hiline.request.ReportRequest
import com.example.hiline.response.CommentResponse
import com.example.hiline.response.FavResponse
import com.example.hiline.response.ForumFavResponse
import com.example.hiline.response.KomenResponse
import com.example.hiline.response.ReportResponse
import com.example.hiline.response.ReportsResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ForumService {
    @GET("/api/forums?")
    fun getForums(
        @Query("page") page: Int,
        @Header("Authorization") aToken: String?
    ): Call<ForumsResponse>

    @GET("/api/forums?")
    fun getForumsFilter(
        @Query("page") page: Int,
        @Query("keyword") keyword: String,
        @Header("Authorization") aToken: String?
    ): Call<ForumsResponse>

    @GET("/api/forums?sort_by=favorite_count")
    fun getForumsPop(
        @Query("page") page: Int,
        @Header("Authorization") aToken: String?
    ): Call<ForumsResponse>

    @GET("/api/forums/favorite")
    fun getForumsFav(
        @Header("Authorization") aToken: String?
    ): Call<ForumFavResponse>

    @POST("/api/forum/{id}/favorite")
    fun favForum(
        @Path("id") id: String,
        @Header("Authorization") aToken: String?
    ): Call<FavResponse>

    @GET("/api/forum/{id}")
    fun getForumById(
        @Path("id") id: String,
        @Header("Authorization") aToken: String?
    ): Call<CommentResponse>

    @POST("/api/forum/{id}/comment")
    fun createComment(
        @Path("id") id: String,
        @Header("Authorization") aToken: String?,
        @Body commentRequest: CommentRequest
    ): Call<KomenResponse>

    @POST("/api/forum/comment/{id}/like")
    fun likeComment(
        @Path("id") id: String,
        @Header("Authorization") aToken: String?
    ): Call<FavResponse>

    @DELETE("/api/forum/comment/{id}")
    fun deleteComment(
        @Path("id") id: String,
        @Header("Authorization") aToken: String?
    ): Call<FavResponse>

    @DELETE("/api/forum/{id}")
    fun deleteForum(
        @Path("id") id: String,
        @Header("Authorization") aToken: String?
    ): Call<FavResponse>

    @POST("/api/forum")
    fun createForum(
        @Header("Authorization") aToken: String?,
        @Body forumRequest: ForumRequest
    ): Call<ForumResponse>

    @PUT("/api/forum/{id}")
    fun updateForum(
        @Path("id") id: String,
        @Header("Authorization") aToken: String?,
        @Body forumRequest: ForumRequest
    ): Call<ForumResponse>

    @POST("/api/forum/comment/{id}/report")
    fun createReport(
        @Path("id") id: String,
        @Header("Authorization") aToken: String?,
        @Body reportRequest: ReportRequest
    ): Call<ReportsResponse>

    @GET("/api/forum/comment/report/user")
    fun getReportsUser(
        @Header("Authorization") aToken: String?
    ): Call<ReportsResponse>

    @GET("/api/forum/comment/report")
    fun getReportsAdmin(
        @Header("Authorization") aToken: String?
    ): Call<ReportsResponse>

    @GET("/api/forum/comment/report/{id}")
    fun getReport(
        @Path("id") id: String,
        @Header("Authorization") aToken: String?
    ): Call<ReportResponse>

    @DELETE("/api/forum/comment/report/{id}")
    fun deleteReport(
        @Path("id") id: String,
        @Header("Authorization") aToken: String?
    ): Call<ReportResponse>
}