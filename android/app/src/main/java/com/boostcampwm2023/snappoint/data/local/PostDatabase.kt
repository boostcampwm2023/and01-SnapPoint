package com.boostcampwm2023.snappoint.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.boostcampwm2023.snappoint.data.local.dao.PostDao
import com.boostcampwm2023.snappoint.data.local.entity.SerializedPost

@Database(entities = [SerializedPost::class], version = 1)
abstract class PostDatabase: RoomDatabase() {
    abstract fun getPostDao(): PostDao
}