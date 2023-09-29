package com.example.hiline.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ReportResponse {
    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("code")
    @Expose
    var code: Int? = null

    @SerializedName("data")
    @Expose
    var data: Data? = null

    class Data{
        @SerializedName("id")
        @Expose
        var id: String? = null

        @SerializedName("comment")
        @Expose
        var comment: Comment? = null

        @SerializedName("pelapor")
        @Expose
        var pelapor: Pelapor? = null

        @SerializedName("terlapor")
        @Expose
        var terlapor: Terlapor? = null

        @SerializedName("message")
        @Expose
        var message: String? = null

        @SerializedName("view_history")
        @Expose
        var view_history: Boolean? = null

        @SerializedName("hour")
        @Expose
        var hour: String? = null

        @SerializedName("date")
        @Expose
        var date: String? = null

        @SerializedName("comment_deleted")
        @Expose
        var comment_deleted: Boolean? = null
    }

    class Comment{
        @SerializedName("id")
        @Expose
        var id: String? = null

        @SerializedName("user")
        @Expose
        var user: User? = null

        @SerializedName("forum_id")
        @Expose
        var forumId: String? = null

        @SerializedName("comment")
        @Expose
        var comment: String? = null

        @SerializedName("like_count")
        @Expose
        var likeCount: Int? = null

        @SerializedName("is_like")
        @Expose
        var isLike: Boolean? = null

        @SerializedName("is_me")
        @Expose
        var isMe: Boolean? = null
    }

    class Pelapor{
        @SerializedName("id")
        @Expose
        var id: String? = null

        @SerializedName("username")
        @Expose
        var username: String? = null

        @SerializedName("name")
        @Expose
        var name: String? = null

        @SerializedName("email")
        @Expose
        var email: String? = null

        @SerializedName("tanggal_lahir")
        @Expose
        var tanggalLahir: String? = null

        @SerializedName("role")
        @Expose
        var role: String? = null

        @SerializedName("image")
        @Expose
        var image: String? = null

        @SerializedName("point")
        @Expose
        var point: Point? = null
    }

    class Terlapor{
        @SerializedName("id")
        @Expose
        var id: String? = null

        @SerializedName("username")
        @Expose
        var username: String? = null

        @SerializedName("name")
        @Expose
        var name: String? = null

        @SerializedName("email")
        @Expose
        var email: String? = null

        @SerializedName("tanggal_lahir")
        @Expose
        var tanggalLahir: String? = null

        @SerializedName("role")
        @Expose
        var role: String? = null

        @SerializedName("image")
        @Expose
        var image: String? = null

        @SerializedName("point")
        @Expose
        var point: Point? = null
    }

    class User{
        @SerializedName("id")
        @Expose
        var id: String? = null

        @SerializedName("username")
        @Expose
        var username: String? = null

        @SerializedName("name")
        @Expose
        var name: String? = null

        @SerializedName("email")
        @Expose
        var email: String? = null

        @SerializedName("tanggal_lahir")
        @Expose
        var tanggalLahir: String? = null

        @SerializedName("role")
        @Expose
        var role: String? = null

        @SerializedName("image")
        @Expose
        var image: String? = null

        @SerializedName("point")
        @Expose
        var point: Point? = null
    }

    class Point{
        @SerializedName("id")
        @Expose
        var id: String? = null

        @SerializedName("user_id")
        @Expose
        var user_id: String? = null

        @SerializedName("point")
        @Expose
        var point: Int? = null

        @SerializedName("grade")
        @Expose
        var grade: String? = null
    }
}