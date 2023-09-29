package com.example.hiline.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class HospitalResponse {
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

        @SerializedName("name")
        @Expose
        var name: String? = null

        @SerializedName("address")
        @Expose
        var address: String? = null

        @SerializedName("wilayah")
        @Expose
        var wilayah: Wilayah? = null

        class Wilayah {
            @SerializedName("serial")
            @Expose
            var serial: String? = null

            @SerializedName("province")
            @Expose
            var province: String? = null

            @SerializedName("city")
            @Expose
            var city: String? = null
        }

        @SerializedName("phone")
        @Expose
        var phone: String? = null

        @SerializedName("image")
        @Expose
        var image: String? = null

        @SerializedName("latitude")
        @Expose
        var latitude: Double? = null

        @SerializedName("longitude")
        @Expose
        var longitude: Double? = null

        @SerializedName("distance")
        @Expose
        var distance: Double? = null

    }
}