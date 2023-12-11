package com.boostcampwm2023.snappoint.presentation.util

import kotlin.random.Random

object CacheFileNameMaker {
    private var names: Set<String> = mutableSetOf()

    fun getRandomName(): String{
        val random = Random
        var temp = ""
        while(true){
            temp = ""
            repeat(10){
                temp += random.nextInt(0, 10)
            }
            if(names.contains(temp).not()) break
        }
        names =  names.plus(temp)
        return temp
    }
}