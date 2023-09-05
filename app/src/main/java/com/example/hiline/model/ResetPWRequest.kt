package com.example.hiline.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ResetPWRequest {
    @SerializedName("new_password")
    @Expose
    var new_password: String? = null

    @SerializedName("confirm_password")
    @Expose
    var confirm_password: String? = null
}