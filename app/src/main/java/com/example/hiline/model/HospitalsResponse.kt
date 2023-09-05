package com.example.hiline.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class HospitalsResponse {
    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("code")
    @Expose
    var code: Int? = null

    @SerializedName("data")
    @Expose
    var data: List<hospital>? = null

    class hospital{
        @SerializedName("id")
        @Expose
        var id: String? = null

        @SerializedName("nama")
        @Expose
        var nama: String? = null

        @SerializedName("kota")
        @Expose
        var kota: String? = null

        @SerializedName("provinsi")
        @Expose
        var provinsi: String? = null

        @SerializedName("alamat")
        @Expose
        var alamat: String? = null

        @SerializedName("telepon")
        @Expose
        var telepon: String? = null

        @SerializedName("image")
        @Expose
        var image: String? = null

        @SerializedName("latitude")
        @Expose
        var latitude: Double? = null

        @SerializedName("longitude")
        @Expose
        var longitude: Double? = null

        @SerializedName("jarak")
        @Expose
        var jarak: Double? = null
    }

}