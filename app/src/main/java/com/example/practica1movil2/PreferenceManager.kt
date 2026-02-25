package com.example.practica1movil2

import android.content.Context
import com.google.gson.Gson

class PreferenceManager(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveUser(user: User) {
        val json = gson.toJson(user)
        sharedPreferences.edit().putString("user_data", json).apply()
    }

    fun getUser(): User? {
        val json = sharedPreferences.getString("user_data", null)
        return if (json != null) {
            gson.fromJson(json, User::class.java)
        } else null
    }

    fun saveProducts(products: List<Product>) {
        val json = gson.toJson(products)
        sharedPreferences.edit().putString("products_list", json).apply()
    }

    fun getProducts(): MutableList<Product> {
        val json = sharedPreferences.getString("products_list", null)
        return if (json != null) {
            val itemType = object : com.google.gson.reflect.TypeToken<MutableList<Product>>() {}.type
            gson.fromJson(json, itemType)
        } else mutableListOf()
    }

}