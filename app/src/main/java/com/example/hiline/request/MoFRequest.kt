package com.example.hiline.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class MoFRequest {
    @SerializedName("education_id")
    @Expose
    var education_id: String? = null

    @SerializedName("user_answer")
    @Expose
    var user_answer: Boolean? = null
}