package com.boostcampwm2023.snappoint.data.repository

import kotlinx.coroutines.flow.Flow

interface MediaRepository {
    fun getImage(uri: String): Flow<ByteArray>
}