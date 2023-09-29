package com.example.hiline.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ProvinceResponse {
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
        @SerializedName("serial")
        @Expose
        var serial: String? = null

        @SerializedName("nama")
        @Expose
        var nama: String? = null
    }

}