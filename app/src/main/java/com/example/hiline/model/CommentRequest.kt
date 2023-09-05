package com.example.hiline.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CommentRequest {
    @SerializedName("postingan_id")
    @Expose
    var postingan_id: String? = null

    @SerializedName("message")
    @Expose
    var message: String? = null
}