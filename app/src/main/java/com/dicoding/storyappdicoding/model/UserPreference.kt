package com.dicoding.storyappdicoding.model

import android.content.Context

class UserPreference (context: Context){


    private val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun setUser(value: UserModel){
        val editor = preferences.edit()
        editor.putString(NAME_KEY, value.name)
        editor.putString(TOKEN_KEY, value.token)
        editor.putBoolean(IS_LOGIN, value.isLogin)
        editor.apply()
    }

    fun getUser(): UserModel{
        val model = UserModel()
        model.name = preferences.getString(NAME_KEY, "")
        model.token = preferences.getString(TOKEN_KEY,"")
        model.isLogin = preferences.getBoolean(IS_LOGIN, false)

        return model
    }

    fun deleteUser(){
        val editor = preferences.edit().clear()
        editor.apply()
    }

    companion object{
        private const val PREFS_NAME = "user_pref"
        private const val NAME_KEY = "name"
        private const val TOKEN_KEY = "token"
        private const val IS_LOGIN = "isLogin"

        @Suppress("KotlinConstantConditions")
        fun getUserPreference(context: Context): UserPreference {
            var instance: UserPreference? = null
            if (instance == null) {
                instance = UserPreference(context)
            }
            return instance
        }
    }

}