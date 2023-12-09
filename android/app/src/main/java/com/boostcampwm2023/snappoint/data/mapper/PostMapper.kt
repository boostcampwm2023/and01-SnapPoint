package com.boostcampwm2023.snappoint.data.mapper

import com.boostcampwm2023.snappoint.data.remote.model.BlockType
import com.boostcampwm2023.snappoint.data.remote.model.File
import com.boostcampwm2023.snappoint.data.remote.model.PostBlock
import com.boostcampwm2023.snappoint.data.remote.model.response.GetPostResponse
import com.boostcampwm2023.snappoint.presentation.model.PositionState
import com.boostcampwm2023.snappoint.presentation.model.PostBlockCreationState
import com.boostcampwm2023.snappoint.presentation.model.PostBlockState
import com.boostcampwm2023.snappoint.presentation.model.PostSummaryState

fun PostBlock.asPostBlockState(): PostBlockState {
    return when {
        this.files.isNullOrEmpty() -> {
            PostBlockState.TEXT(
                uuid = blockUuid!!,
                content = this.content,
            )
        }

        this.files[0].mimeType!!.startsWith("image") -> {
            PostBlockState.IMAGE(
                uuid = blockUuid!!,
                description = this.content,
                content = this.files[0].url!!,
                position = this.asPositionState(),
                fileUuid = this.files[0].fileUuid
            )
        }

        else -> {
            PostBlockState.VIDEO(
                uuid = blockUuid!!,
                description = this.content,
                content = this.files[0].url!!,
                position = this.asPositionState(),
                fileUuid = this.files[0].fileUuid
            )
        }
    }
}

fun PostBlockCreationState.asPostBlock(): PostBlock {
    return when (this) {
        is PostBlockCreationState.TEXT -> {
            PostBlock(
                blockUuid = this.uuid,
                type = BlockType.TEXT.type,
                content = this.content,
            )
        }

        is PostBlockCreationState.IMAGE -> {
            PostBlock(
                blockUuid = this.uuid,
                content = this.description,
                type = BlockType.MEDIA.type,
                latitude = this.position.latitude,
                longitude = this.position.longitude,
                files = listOf(File(fileUuid = this.fileUuid)),
            )
        }

        is PostBlockCreationState.VIDEO -> {
            PostBlock(
                blockUuid = this.uuid,
                content = this.description,
                type = BlockType.MEDIA.type,
                latitude = this.position.latitude,
                longitude = this.position.longitude,
                files = listOf(File(fileUuid = this.fileUuid)),
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

fun List<GetPostResponse>.asPostSummaryState(): List<PostSummaryState> {
    return this.map{ response ->
        PostSummaryState(
            uuid = response.postUuid,
            title = response.title,
            author = "",
            timeStamp = response.createdAt,
            summary = response.summary,
            postBlocks = response.blocks.map { it.asPostBlockState() }
        )
    }
}