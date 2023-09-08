package com.example.hiline

import com.example.hiline.api.AuthService
import com.google.gson.GsonBuilder
import okhttp3.Authenticator
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TokenAuthenticator : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        // Check if the response is a 401 Unauthorized
        if (response.code == 401) {
            // Refresh the OAuth token here
            val newToken = refreshToken() // Implement this method to refresh your token
            if (newToken != null) {
                // Retry the request with the new token
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $newToken")
                    .build()
            }
        }
        return null // If token refresh fails, return null to indicate that authentication is not possible
    }

    private fun refreshToken(): String? {
        // Implement your token refresh logic here
        // This function should return the new OAuth token or null if refresh fails
        // You can use your existing Retrofit instance to make a token refresh API call
        val retrofit = Retro().getRetroAuthUrl()
        val authService = retrofit.create(AuthService::class.java)
        val refreshTokenResponse = authService.refreshToken().execute()

        if (refreshTokenResponse.isSuccessful) {
            // Token refresh successful, return the new token
            val newToken = refreshTokenResponse.body()?.data?.access_token
            return newToken
        }

        return null // Token refresh failed
    }
}
