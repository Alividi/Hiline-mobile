package com.example.hiline.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CityResponse {
    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("code")
    @Expose
    var code: Int? = null

    @SerializedName("data")
    @Expose
    var data: List<Data>? = null
    class Data {
        @SerializedName("nama")
        @Expose
        var nama: String? = null

        @SerializedName("serial")
        @Expose
        var serial: String? = null
    }
}