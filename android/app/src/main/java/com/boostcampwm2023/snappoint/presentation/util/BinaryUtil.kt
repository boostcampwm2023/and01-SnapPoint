package com.boostcampwm2023.snappoint.presentation.util

fun byteArrayToBinaryString(b: ByteArray): String {
    val sb = StringBuilder()
    for (i in b.indices) {
        sb.append(byteToBinaryString(b[i]))
    }
    return sb.toString()
}

fun byteToBinaryString(n: Byte): String {
    val sb = StringBuilder("00000000")
    for (bit in 0..7) {
        if (n.toInt() shr bit and 1 > 0) {
            sb.setCharAt(7 - bit, '1')
        }
    }
    return sb.toString()
}