package com.nitj.nitj.firebaseNotifications

import com.nitj.nitj.firebaseNotifications.Constants.Companion.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {

    companion object{
        private val retrofit by lazy {
            Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build()
        }

       val api: NotificationAPI by lazy {
           retrofit.create(NotificationAPI::class.java)
       }
    }


}