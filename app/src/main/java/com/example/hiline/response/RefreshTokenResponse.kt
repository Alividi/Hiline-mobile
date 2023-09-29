package com.example.hiline.response
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class RefreshTokenResponse {
    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("code")
    @Expose
    var code: Int? = null

    @SerializedName("data")
    @Expose
    var data: Data? = null

    class Data {
        @SerializedName("access_token")
        @Expose
        var access_token: String? = null

        @SerializedName("refresh_token")
        @Expose
        var refresh_token: String? = null

        @SerializedName("user")
        @Expose
        var user: User? = null
    }
    class User{
        @SerializedName("id")
        @Expose
        var id: String? = null

        @SerializedName("username")
        @Expose
        var username: String? = null

        @SerializedName("name")
        @Expose
        var name: String? = null

        @SerializedName("email")
        @Expose
        var email: String? = null

        @SerializedName("role")
        @Expose
        var role: String? = null

        @SerializedName("tanggal_lahir")
        @Expose
        var tanggal_lahir: String? = null

        @SerializedName("image")
        @Expose
        var image: String? = null

        @SerializedName("user_point")
        @Expose
        var user_point: UserPoint? = null
    }

    class UserPoint{
        @SerializedName("id")
        @Expose
        var id: String? = null

        @SerializedName("user_id")
        @Expose
        var user_id: String? = null

        @SerializedName("point")
        @Expose
        var point: Int? = null

        @SerializedName("grade")
        @Expose
        var grade: String? = null
    }
}
