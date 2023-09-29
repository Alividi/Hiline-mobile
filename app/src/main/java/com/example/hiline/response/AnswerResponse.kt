package com.example.hiline.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AnswerResponse {
    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("code")
    @Expose
    var code: Int? = null

    @SerializedName("data")
    @Expose
    var data: Data? = null

    class Data{
        @SerializedName("id")
        @Expose
        var id: String? = null

        @SerializedName("user_id")
        @Expose
        var userId: String? = null

        @SerializedName("education_id")
        @Expose
        var educationId: String? = null

        @SerializedName("score")
        @Expose
        var score: Int? = null

        @SerializedName("user_answer")
        @Expose
        var userAnswer: Boolean? = null
    }
}