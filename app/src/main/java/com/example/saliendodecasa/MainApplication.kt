package com.example.saliendodecasa

import android.app.Application
import com.example.saliendodecasa.checklist.data.LocalItemRepositoryImpl
import com.example.saliendodecasa.core.database.LocalAppDatabase

class MainApplication : Application() {
    val database by lazy {
        LocalAppDatabase.getDatabase(this)
    }

    val itemRepository by lazy {
        LocalItemRepositoryImpl(database.itemDao())
    }
}