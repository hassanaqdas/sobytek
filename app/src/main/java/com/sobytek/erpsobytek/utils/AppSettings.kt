package com.sobytek.erpsobytek.utils

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.sobytek.erpsobytek.model.User

class AppSettings(context:Context) {

    private val appSharedPrefs:String = "sobytek_erp_prefs"
    var sharedPrefs:SharedPreferences = context.getSharedPreferences(appSharedPrefs,Activity.MODE_PRIVATE)
    private lateinit var prefsEditor: SharedPreferences.Editor

    fun getString(key: String): String? {
        return sharedPrefs.getString(key, "")
    }

    fun getInt(key: String): Int {
        return sharedPrefs.getInt(key, 0)
    }

    fun getLong(key: String): Long {
        return sharedPrefs.getLong(key, 0)
    }

    fun getBoolean(key: String): Boolean {
        return sharedPrefs.getBoolean(key, false)
    }

    fun getUser(key: String): User? {
        val value = sharedPrefs.getString(key, "")
        return Gson().fromJson(value,User::class.java)
    }

    fun putUser(key: String, user: User) {
        val value = Gson().toJson(user)
        this.prefsEditor = sharedPrefs.edit()
        prefsEditor.putString(key, value)
        prefsEditor.commit()
    }

    fun putString(key: String, value: String) {
        this.prefsEditor = sharedPrefs.edit()
        prefsEditor.putString(key, value)
        prefsEditor.commit()
    }

    fun putInt(key: String, value: Int) {
        this.prefsEditor = sharedPrefs.edit()
        prefsEditor.putInt(key, value)
        prefsEditor.commit()
    }

    fun putLong(key: String, value: Long) {
        this.prefsEditor = sharedPrefs.edit()
        prefsEditor.putLong(key, value)
        prefsEditor.commit()
    }

    fun putBoolean(key: String, value: Boolean) {
        this.prefsEditor = sharedPrefs.edit()
        prefsEditor.putBoolean(key, value)
        prefsEditor.commit()
    }

    fun remove(key: String) {
        this.prefsEditor = sharedPrefs.edit()
        prefsEditor.remove(key)
        prefsEditor.commit()
    }

    fun clear() {
        this.prefsEditor = sharedPrefs.edit()
        prefsEditor.clear()
        prefsEditor.commit()
    }
}