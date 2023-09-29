package com.example.hiline.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class HospitalRequest {
    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("address")
    @Expose
    var address: String? = null

    @SerializedName("phone")
    @Expose
    var phone: String? = null

    @SerializedName("wilayah_serial")
    @Expose
    var wilayah_serial: String? = null

    @SerializedName("latitude")
    @Expose
    var latitude: Double? = null

    @SerializedName("longitude")
    @Expose
    var longitude: Double? = null

    @SerializedName("image")
    @Expose
    var image: String? = null
}