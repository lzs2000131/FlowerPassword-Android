package com.example.flowerpassword

import android.app.Application
import com.example.flowerpassword.data.AppDatabase
import com.example.flowerpassword.data.PreferencesManager

class FlowerPasswordApp : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val preferencesManager by lazy { PreferencesManager(this) }
}
