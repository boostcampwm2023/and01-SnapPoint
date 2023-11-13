package com.boostcampwm2023.snappoint

import androidx.test.rule.GrantPermissionRule
import com.boostcampwm2023.snappoint.presentation.util.MetadataUtil
import org.junit.Rule
import org.junit.Test
import java.io.File

class MediaRepositoryTest {

    @JvmField
    @Rule
    val storagePermissionRule : GrantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.READ_MEDIA_IMAGES)

    @JvmField
    @Rule
    val locationPermissionRule : GrantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_MEDIA_LOCATION)

    private val imageFileWithGPS = "/storage/emulated/0/Pictures/IMG_20231113_022342.jpg"
    private val imageFileWithoutGPS = "/storage/emulated/0/Pictures/IMG_20231113_130850.jpg"

    @Test
    fun 미디어_파일을_성공적으로_가져온_경우(){

    }

    @Test
    fun 파일이_존재하는지_확인() {
        assert(File(imageFileWithGPS).exists())
        assert(File(imageFileWithoutGPS).exists())
    }

    @Test
    fun 미디어_파일의_메타데이터가_존재하는_경우() {
        val result = MetadataUtil.extractPosition(imageFileWithGPS)
        assert(result.isSuccess)
    }

    @Test
    fun 미디어_파일의_매타데이터가_없는_경우() {
        val result = MetadataUtil.extractPosition(imageFileWithoutGPS)
        assert(result.isFailure)

        result.onFailure {
            assert(it.message == "there is no location data.")
        }
    }
}