package com.example.hiline.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ReportRequest {
    @SerializedName("comment_id")
    @Expose
    var comment_id: String? = null

    @SerializedName("message")
    @Expose
    var message: String? = null
}