package com.example.hiline

import android.content.Context
import android.content.SharedPreferences

class PrefManager(var context: Context) {
    val PRIVATE_MODE = 0
    private val PREF_NAME = "SharedPreferences"
    private val IS_LOGIN = "is_login"

    var pref: SharedPreferences = context.getSharedPreferences(PREF_NAME,PRIVATE_MODE)
    var editor: SharedPreferences.Editor = pref.edit()

    fun setLogin(isLogin: Boolean){
        editor.putBoolean(IS_LOGIN, isLogin)
        editor.commit()
    }

    fun setId(id: String){
        editor.putString("id", id)
        editor.commit()
    }

    fun setNama(nama: String){
        editor.putString("nama", nama)
        editor.commit()
    }

    fun setUsername(username: String){
        editor.putString("username", username)
        editor.commit()
    }

    fun setAccessToken(token: String){
        editor.putString("acces_token", token)
        editor.commit()
    }

    fun setRefreshToken(token: String){
        editor.putString("refresh_token", token)
        editor.commit()
    }

    fun setEmail(email: String){
        editor.putString("email", email)
        editor.commit()
    }

    fun setRole(role: String){
        editor.putString("role", role)
        editor.commit()
    }

    fun setTanggal(tanggal: String){
        editor.putString("tanggal", tanggal)
        editor.commit()
    }

    fun setPImg(pimg: String){
        editor.putString("pimg", pimg)
        editor.commit()
    }

    fun isLogin(): Boolean{
        return pref.getBoolean(IS_LOGIN, false)
    }

    fun getId(): String?{
        return pref.getString("id", "")
    }

    fun getNama(): String?{
        return pref.getString("nama", "")
    }

    fun getUsername(): String?{
        return pref.getString("username", "")
    }

    fun getAccessToken(): String?{
        return pref.getString("access_token", "")
    }

    fun getRefreshToken(): String?{
        return pref.getString("refresh_token", "")
    }

    fun getEmail(): String?{
        return pref.getString("email", "")
    }

    fun getRole(): String?{
        return pref.getString("role", "")
    }

    fun getTanggal(): String?{
        return pref.getString("tanggal", "")
    }

    fun getPImg(): String?{
        return pref.getString("pimg", "")
    }

    fun removeData(){
        editor.clear()
        editor.commit()
    }

}