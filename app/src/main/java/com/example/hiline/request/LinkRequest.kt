package com.example.hiline.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LinkRequest {
    @SerializedName("education_id")
    @Expose
    var education_id: String? = null
}