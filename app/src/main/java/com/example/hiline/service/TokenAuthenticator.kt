package com.example.hiline.service

import android.content.Intent
import android.util.Log
import com.example.hiline.admin.profile.ProfileAdminInfoActivity
import com.example.hiline.response.RefreshTokenResponse
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.Call

class TokenAuthenticator(private val prefManager: PrefManager) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.code == 401) {
            val newToken = refreshToken()
            if (newToken != null) {
                prefManager.setAccessToken(newToken)
                Log.e("TokenAuthenticator", "Token refreshed successfully")
                return response.request.newBuilder()
                    .header("Authorization", newToken)
                    .build()
            }
            else {
                Log.e("TokenAuthenticator", "Token refresh failed")
                Log.e("Message: ", response.message)
            }
        }
        return null
    }

    private fun refreshToken(): String? {
        val retrofit = Retro().getRetroAuthUrl()
        val authService = retrofit.create(AuthService::class.java)
        val refreshTokenCall: Call<RefreshTokenResponse> = authService.refreshToken(prefManager.getRefreshToken())

        try {
            val refreshTokenResponse: retrofit2.Response<RefreshTokenResponse> = refreshTokenCall.execute()

            if (refreshTokenResponse.isSuccessful) {
                val newToken = refreshTokenResponse.body()?.data?.access_token
                return newToken
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }


}
