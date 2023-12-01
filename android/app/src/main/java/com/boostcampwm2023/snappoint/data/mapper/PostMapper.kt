package com.boostcampwm2023.snappoint.data.mapper

import com.boostcampwm2023.snappoint.data.remote.model.BlockType
import com.boostcampwm2023.snappoint.data.remote.model.File
import com.boostcampwm2023.snappoint.data.remote.model.PostBlock
import com.boostcampwm2023.snappoint.data.remote.model.response.GetPostResponse
import com.boostcampwm2023.snappoint.presentation.model.PositionState
import com.boostcampwm2023.snappoint.presentation.model.PostBlockState
import com.boostcampwm2023.snappoint.presentation.model.PostSummaryState

fun PostBlock.asPostBlockState(): PostBlockState {
    return when(type){
        BlockType.TEXT.type -> {
            PostBlockState.TEXT(
                uuid = blockUuid!!,
                content = this.content,
            )
        }
        else -> {
            if(this.files!![0].mimeType!!.startsWith("image")){
                PostBlockState.IMAGE(
                    uuid = blockUuid!!,
                    description = this.content,
                    content = this.files[0].url!!,
                    position = this.asPositionState(),
                )
            } else {
                PostBlockState.VIDEO(
                    uuid = blockUuid!!,
                    description = this.content,
                    content = this.files[0].url!!,
                    position = this.asPositionState(),
                )
            }

        }

    }
}

fun PostBlockState.asPostBlock(): PostBlock {
    return when(this){
        is PostBlockState.TEXT -> {
            PostBlock(
                type = BlockType.TEXT.type,
                content = this.content,
            )
        }
        is PostBlockState.IMAGE -> {
            PostBlock(
                content = this.content,
                type = BlockType.MEDIA.type,
                latitude = this.position.latitude,
                longitude = this.position.longitude,
                files = listOf(File(fileUuid = "this is file's uuid")),
            )
        }
        is PostBlockState.VIDEO -> {
            PostBlock(
                content = this.description,
                type = BlockType.MEDIA.type,
                latitude = this.position.latitude,
                longitude = this.position.longitude,
                files = listOf(File(fileUuid = "this is file's uuid")),
            )
        }
    }
}

fun PostBlock.asPositionState(): PositionState {
    return PositionState(
        latitude = this.latitude!!,
        longitude = this.longitude!!
    )
}

fun GetPostResponse.asPostSummaryState(): PostSummaryState {
    return PostSummaryState(
        uuid = this.postUuid,
        title = this.title,
        author = "",
        timeStamp = this.createdAt,
        summary = this.summary,
        postBlocks = this.blocks.map { it.asPostBlockState() }
    )
}