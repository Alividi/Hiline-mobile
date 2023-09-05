package com.example.hiline.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CommentResponse {
    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("code")
    @Expose
    var code: Int? = null

    @SerializedName("data")
    @Expose
    var data: datas? = null

    class datas{
        @SerializedName("id")
        @Expose
        var id: String? = null

        @SerializedName("user")
        @Expose
        var user: User? = null

        class User{
            @SerializedName("id")
            @Expose
            var id: String? = null

            @SerializedName("nama")
            @Expose
            var nama: String? = null

            @SerializedName("username")
            @Expose
            var username: String? = null

            @SerializedName("email")
            @Expose
            var email: String? = null

            @SerializedName("role")
            @Expose
            var role: String? = null

            @SerializedName("verified")
            @Expose
            var verified: Boolean? = null

            @SerializedName("tanggal_lahir")
            @Expose
            var tanggal_lahir: String? = null

            @SerializedName("profile_image")
            @Expose
            var profile_image: String? = null
        }

        @SerializedName("user_id")
        @Expose
        var user_id: String? = null

        @SerializedName("postingan_id")
        @Expose
        var postingan_id: String? = null

        @SerializedName("message")
        @Expose
        var message: String? = null

        @SerializedName("like_count")
        @Expose
        var like_count: Int? = null
    }
}