package com.boostcampwm2023.snappoint.presentation.util

import android.content.Context
import android.util.Log
import java.io.File
import kotlin.random.Random

object CacheManager {
    private var names: MutableSet<String> = mutableSetOf()

    private fun getRandomName(): String{
        val random = Random
        var temp = ""
        while(true){
            temp = ""
            repeat(10){
                temp += random.nextInt(0, 10)
            }
            if(names.contains(temp).not()) break
        }
        names.add(temp)
        return temp
    }

    fun createExternalCacheFile(context: Context): File {

        Log.d("TAG", "createExternalCacheFile: ${context.cacheDir.path}/video_cache/${getRandomName()}.mp4}")
        val cacheDir = File(context.cacheDir,"video_cache")
        cacheDir.mkdir()
        val file = File(cacheDir, "${getRandomName()}.mp4")

        try{
            if (file.exists() && !file.delete()) {
                throw IllegalStateException("Could not delete the previous export output file")
            }
            if (!file.createNewFile()) {
                throw IllegalStateException("Could not create the export output file")
            }
        } catch (e:Exception){
            Log.d("TAG", "createExternalCacheFile: ${e.message}")
        }
        return file
    }

    fun clearVideoCache(context: Context){
        val cacheDir = File(context.cacheDir,"video_cache")
        val caches = cacheDir.list() ?: return
        caches.forEach {
            File(cacheDir, it).delete()
        }
        names.clear()
        cacheDir.delete()
    }
}