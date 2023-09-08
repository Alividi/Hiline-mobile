package com.example.hiline

import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Retro {
    fun getRetroClientInstance(): Retrofit {
        val gson = GsonBuilder().setLenient().create()
        return Retrofit.Builder()
            .baseUrl("https://hiline.pegelinux.tech/api/v1/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    fun getRetroAuthUrl(): Retrofit {
        val gson = GsonBuilder().setLenient().create()
        val okHttpClient = OkHttpClient.Builder()
            .authenticator(TokenAuthenticator()) // Add the authenticator
            .build()

        return Retrofit.Builder()
            .baseUrl("https://auth.hiline.my.id")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient) // Set the custom OkHttpClient
            .build()
    }

    fun getRetroWilayahUrl(): Retrofit {
        val gson = GsonBuilder().setLenient().create()
        return Retrofit.Builder()
            .baseUrl("https://wilayah.hiline.my.id")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    fun getRetroUserUrl(): Retrofit {
        val gson = GsonBuilder().setLenient().create()
        return Retrofit.Builder()
            .baseUrl("https://user.hiline.my.id")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    fun getRetroHospitalUrl(): Retrofit {
        val gson = GsonBuilder().setLenient().create()
        return Retrofit.Builder()
            .baseUrl("https://hospital.hiline.my.id")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    fun getRetroEduUrl(): Retrofit {
        val gson = GsonBuilder().setLenient().create()
        return Retrofit.Builder()
            .baseUrl("https://edu.hiline.my.id")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
}