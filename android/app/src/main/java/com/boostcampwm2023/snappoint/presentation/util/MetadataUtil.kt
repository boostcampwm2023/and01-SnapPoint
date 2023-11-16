package com.boostcampwm2023.snappoint.presentation.util

import androidx.exifinterface.media.ExifInterface
import com.boostcampwm2023.snappoint.presentation.createpost.PositionState

object MetadataUtil {

    fun extractPosition(path: String): Result<PositionState> {
        return runCatching {
            val latLong = ExifInterface(path).latLong ?: throw Error("there is no location data.")
            PositionState(latLong[0], latLong[1])
        }
    }
}