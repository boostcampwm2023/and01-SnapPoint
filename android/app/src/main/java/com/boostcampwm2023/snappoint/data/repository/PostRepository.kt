package com.boostcampwm2023.snappoint.data.repository

import kotlinx.coroutines.flow.Flow

interface PostRepository {
    fun getImage(uri: String): Flow<ByteArray>
}