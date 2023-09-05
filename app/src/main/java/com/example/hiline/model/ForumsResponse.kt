package com.example.hiline.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ForumsResponse {
    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("code")
    @Expose
    var code: Int? = null

    @SerializedName("data")
    @Expose
    var data: List<datas>? = null

    class datas{
        @SerializedName("id")
        @Expose
        var id: String? = null

        @SerializedName("user")
        @Expose
        var user: users? = null

        class users{
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

        @SerializedName("title")
        @Expose
        var title: String? = null

        @SerializedName("description")
        @Expose
        var description: String? = null

        @SerializedName("favorite_count")
        @Expose
        var favorite_count: Int? = null

        @SerializedName("comment")
        @Expose
        var comment: List<comments>? = null

        class comments{

        }

        @SerializedName("comment_count")
        @Expose
        var comment_count: Int? = null

        @SerializedName("is_favorite")
        @Expose
        var is_favorite: Boolean? = null
    }
}