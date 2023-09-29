package com.example.hiline.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LupaPwRequest {
    @SerializedName("email")
    @Expose
    var email: String? = null
}