package com.boostcampwm2023.snappoint.presentation.videoedit

import android.net.Uri
import android.os.Bundle
import androidx.core.net.toUri
import com.boostcampwm2023.snappoint.R
import com.boostcampwm2023.snappoint.databinding.ActivityVideoEditBinding
import com.boostcampwm2023.snappoint.presentation.base.BaseActivity

class VideoEditActivity : BaseActivity<ActivityVideoEditBinding>(R.layout.activity_video_edit) {

    private var index = 0
    private lateinit var uri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getIntentExtra()
    }

    private fun getIntentExtra() {
        uri = intent.getStringExtra("uri")?.toUri() ?: return
        index = intent.getIntExtra("index",0) ?: 0
    }

}