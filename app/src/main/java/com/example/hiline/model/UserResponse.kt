package com.example.hiline.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UserResponse {
    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("code")
    @Expose
    var code: Int? = null

    @SerializedName("data")
    @Expose
    var data: User? = null

    class User {
        @SerializedName("user")
        @Expose
        var user: Info? = null

        @SerializedName("token")
        @Expose
        var token: String? = null

        class Info{
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
    }
}