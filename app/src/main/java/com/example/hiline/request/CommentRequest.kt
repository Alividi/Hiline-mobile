package com.example.hiline.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CommentRequest {
    @SerializedName("comment")
    @Expose
    var comment: String? = null
}