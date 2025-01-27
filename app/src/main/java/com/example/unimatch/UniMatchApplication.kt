package com.example.unimatch

import android.app.Application
import com.google.firebase.FirebaseApp

class UniMatchApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}