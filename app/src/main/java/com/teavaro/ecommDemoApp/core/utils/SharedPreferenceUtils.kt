package com.teavaro.ecommDemoApp.core.utils

import android.content.Context

object SharedPreferenceUtils {

    private const val STUB_TOKEN = "STUB_TOKEN"
    private const val IS_LOGIN = "IS_LOGIN"
    private const val USER_ID = "USER_ID"

    private fun getSharedPreferences(context: Context) = context.getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE)

    fun getStubToken(context: Context) = getSharedPreferences(context).getString(STUB_TOKEN, null)

    fun setStubToken(context: Context, value: String?) {
        getSharedPreferences(context).edit().putString(STUB_TOKEN, value).apply()
    }

    fun isLogin(context: Context) = getSharedPreferences(context).getBoolean(IS_LOGIN, false)

    fun setLogin(context: Context, value: Boolean) {
        getSharedPreferences(context).edit().putBoolean(IS_LOGIN, value).apply()
    }

    fun clearPreferences(context: Context){
        getSharedPreferences(context).edit().clear()
    }

    fun setUserId(context: Context, value: String?) {
        getSharedPreferences(context).edit().putString(USER_ID, value).apply()
    }

    fun getUserId(context: Context) = getSharedPreferences(context).getString(USER_ID, null)
}