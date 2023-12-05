package com.boostcampwm2023.snappoint.data.local.converter

import androidx.room.TypeConverter
import com.boostcampwm2023.snappoint.data.local.entity.SerializedPost
import com.boostcampwm2023.snappoint.presentation.model.PostSummaryState
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object PostTypeConverter {

    @TypeConverter
    fun serialize(post: PostSummaryState): String {
        return Json.encodeToString(post)
    }

    @TypeConverter
    fun deserialize(json: String): PostSummaryState {
        return Json.decodeFromString(json)
    }
}