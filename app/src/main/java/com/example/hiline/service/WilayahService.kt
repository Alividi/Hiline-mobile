package com.example.hiline.service

import com.example.hiline.response.CityResponse
import com.example.hiline.response.ProvinceResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface WilayahService {
    @GET("/api/wilayah/provinsi")
    fun getProvince(
        @Header("Authorization") aToken: String?
    ): Call<ProvinceResponse>

    @GET("/api/wilayah/kota/{province}")
    fun getCity(
        @Path("province") id: String,
        @Header("Authorization") aToken: String?
    ): Call<CityResponse>
}