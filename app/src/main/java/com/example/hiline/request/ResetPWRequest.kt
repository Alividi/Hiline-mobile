package com.example.hiline.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ResetPWRequest {
    @SerializedName("token")
    @Expose
    var token: String? = null

    @SerializedName("password")
    @Expose
    var password: String? = null

    @SerializedName("confirm_password")
    @Expose
    var confirm_password: String? = null
}