package com.example.hiline.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class EditProfileAdminRequest {
    @SerializedName("image")
    @Expose
    var image: String? = null

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