package com.example.hiline.service

import com.example.hiline.request.HospitalRequest
import com.example.hiline.response.HospitalResponse
import com.example.hiline.response.HospitalsResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface HospitalService {
    @GET("/api/hospitals?")
    fun getHospitals(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("max_distance") maxDistance: Int,
        @Query("wilayah_serial") wilayahSerial: String,
        @Query("keyword") keyword: String?,
        @Query("page") page: Int,
        @Query("page_size") pageSize: Int,
        @Header("Authorization") accessToken: String?
    ): Call<HospitalsResponse>

    @POST("/api/hospital")
    fun createHospital(
        @Body request: HospitalRequest,
        @Header("Authorization") aToken: String?
    ): Call<HospitalResponse>

    @PUT("/api/hospital/{id}")
    fun updateHospital(
        @Path("id") id: String,
        @Body request: HospitalRequest,
        @Header("Authorization") aToken: String?
    ): Call<HospitalResponse>

    @DELETE("/api/hospital/{id}")
    fun deleteHospital(
        @Path("id") id: String,
        @Header("Authorization") aToken: String?
    ): Call<HospitalResponse>

}