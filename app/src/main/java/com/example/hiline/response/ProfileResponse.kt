package com.example.hiline.response

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
        @SerializedName("name")
        @Expose
        var name: String? = null

        @SerializedName("username")
        @Expose
        var username: String? = null

        @SerializedName("email")
        @Expose
        var email: String? = null

        @SerializedName("role")
        @Expose
        var role: String? = null

        @SerializedName("tanggal_lahir")
        @Expose
        var tanggal_lahir: String? = null

        @SerializedName("image")
        @Expose
        var image: String? = null
    }
}