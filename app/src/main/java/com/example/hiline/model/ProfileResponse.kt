package com.example.hiline.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ProfileResponse {
    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("code")
    @Expose
    var code: Int? = null

    @SerializedName("data")
    @Expose
    var data: Datas? = null

    class Datas{
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