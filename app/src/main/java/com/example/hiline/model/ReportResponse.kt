package com.example.hiline.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ReportResponse {
    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("code")
    @Expose
    var code: Int? = null

    @SerializedName("data")
    @Expose
    var data: datas? = null

    class datas{
        @SerializedName("id")
        @Expose
        var id: String? = null

        @SerializedName("comment_id")
        @Expose
        var comment_id: String? = null

        @SerializedName("pelapor")
        @Expose
        var pelapor: Pelapor? = null

        class Pelapor{
            @SerializedName("id")
            @Expose
            var id: String? = null

            @SerializedName("nama")
            @Expose
            var nama: String? = null

            @SerializedName("username")
            @Expose
            var username: String? = null

            @SerializedName("email")
            @Expose
            var email: String? = null

            @SerializedName("role")
            @Expose
            var role: String? = null

            @SerializedName("verified")
            @Expose
            var verified: Boolean? = null

            @SerializedName("tanggal_lahir")
            @Expose
            var tanggal_lahir: String? = null

            @SerializedName("profile_image")
            @Expose
            var profile_image: String? = null
        }

        @SerializedName("terlapor")
        @Expose
        var terlapor: Terlapor? = null

        class Terlapor{
            @SerializedName("id")
            @Expose
            var id: String? = null

            @SerializedName("nama")
            @Expose
            var nama: String? = null

            @SerializedName("username")
            @Expose
            var username: String? = null

            @SerializedName("email")
            @Expose
            var email: String? = null

            @SerializedName("role")
            @Expose
            var role: String? = null

            @SerializedName("verified")
            @Expose
            var verified: Boolean? = null

            @SerializedName("tanggal_lahir")
            @Expose
            var tanggal_lahir: String? = null

            @SerializedName("profile_image")
            @Expose
            var profile_image: String? = null
        }

        @SerializedName("message")
        @Expose
        var message: String? = null

        @SerializedName("comment_deleted")
        @Expose
        var comment_deleted: Boolean? = null

        @SerializedName("terproses")
        @Expose
        var terproses: Boolean? = null

        @SerializedName("jam")
        @Expose
        var jam: String? = null

        @SerializedName("tanggal")
        @Expose
        var tanggal: String? = null
    }
}