package com.example.hiline.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class EditProfileAdminRequest {
    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("username")
    @Expose
    var username: String? = null

    @SerializedName("email")
    @Expose
    var email: String? = null
}