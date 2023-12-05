package com.boostcampwm2023.snappoint.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.boostcampwm2023.snappoint.data.local.converter.PostTypeConverter
import com.boostcampwm2023.snappoint.data.local.dao.PostDao
import com.boostcampwm2023.snappoint.data.local.entity.SerializedPost

@Database(entities = [SerializedPost::class], version = 1)
@TypeConverters(PostTypeConverter::class)
abstract class PostDatabase: RoomDatabase() {
    abstract fun getPostDao(): PostDao
}