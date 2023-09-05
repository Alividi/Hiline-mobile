package com.example.hiline.api

import com.example.hiline.model.HospitalResponse
import com.example.hiline.model.HospitalsResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface HospitalApi {
    @GET("hospitals/{latitude}/{longitude}")
    fun getHospitals (
        @Path("latitude") latitude: Double,
        @Path("longitude") longitude: Double
    ): Call<HospitalsResponse>

    @Multipart
    @POST("hospital")
    fun postHospital(
        @Part("name") name: RequestBody,
        @Part("city") city: RequestBody,
        @Part("province") province: RequestBody,
        @Part("location") location: RequestBody,
        @Part("telp") telp: RequestBody,
        @Part("latitude") latitude: RequestBody,
        @Part("longitude") longitude: RequestBody,
        @Part image: MultipartBody.Part,
        @Header("Authorization") token_auth: String?
    ): Call<HospitalResponse>

    @DELETE("hospital/{id}")
    fun deleteHospital(
        @Path("id") id: String,
        @Header("Authorization") token_auth: String?
    ): Call<HospitalResponse>

    @Multipart
    @PUT("hospital/{id}")
    fun putHospital(
        @Path("id") id: String,
        @Part("name") name: RequestBody,
        @Part("city") city: RequestBody,
        @Part("province") province: RequestBody,
        @Part("location") location: RequestBody,
        @Part("telp") telp: RequestBody,
        @Part("latitude") latitude: RequestBody,
        @Part("longitude") longitude: RequestBody,
        @Part image: MultipartBody.Part,
        @Header("Authorization") token_auth: String?
    ): Call<HospitalResponse>

}