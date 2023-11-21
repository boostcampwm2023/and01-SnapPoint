package com.boostcampwm2023.snappoint.data.mapper

import android.net.Uri
import com.boostcampwm2023.snappoint.data.remote.model.BlockType
import com.boostcampwm2023.snappoint.data.remote.model.Position
import com.boostcampwm2023.snappoint.data.remote.model.PostBlock
import com.boostcampwm2023.snappoint.presentation.model.PositionState
import com.boostcampwm2023.snappoint.presentation.model.PostBlockState

fun PostBlock.asPostBlockState(): PostBlockState {
    return when(type){
        BlockType.TEXT -> {
            PostBlockState.STRING(
                content = this.content
            )
        }
        BlockType.IMAGE -> {
            PostBlockState.IMAGE(
                content = this.content,
                position = this.position!!.asPositionState(),
                uri = Uri.EMPTY
            )
        }
        BlockType.VIDEO -> TODO()
        else -> {
            PostBlockState.STRING(
                content = ""
            )
        }
    }
}

fun PostBlockState.asPostBlock(): PostBlock {
    return when(this){
        is PostBlockState.STRING -> {
            PostBlock(
                type = BlockType.TEXT,
                content = this.content,
            )
        }
        is PostBlockState.IMAGE -> {
            PostBlock(
                type = BlockType.IMAGE,
                content = this.content,
                fileContent = null,
                position = this.position.asPosition()
            )
        }
        is PostBlockState.VIDEO -> {
            PostBlock(
                type = BlockType.VIDEO,
                content = this.content,
                fileContent = null,
                position = this.position.asPosition()
            )
        }
    }
}

fun Position.asPositionState(): PositionState {
    return PositionState(
        latitude = this.x,
        longitude = this.y
    )
}

fun PositionState.asPosition(): Position{
    return Position(
        x = this.latitude,
        y = this.longitude
    )
}