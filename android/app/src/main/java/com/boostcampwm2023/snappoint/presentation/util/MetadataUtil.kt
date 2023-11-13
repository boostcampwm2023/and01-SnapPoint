package com.boostcampwm2023.snappoint.presentation.util

import androidx.exifinterface.media.ExifInterface
import com.boostcampwm2023.snappoint.presentation.Position

object MetadataUtil {

    fun extractPosition(path: String): Result<Position> {
        return runCatching {
            val latLong = ExifInterface(path).latLong ?: throw Error("there is no location data.")
            Position(latLong[0], latLong[1])
        }
    }
}