package com.example.hiline.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ForumRequest {
    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("description")
    @Expose
    var description: String? = null
}