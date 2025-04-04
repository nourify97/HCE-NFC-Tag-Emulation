package com.nourify.ndeftagemulation.util

val HEX_CHARS = "0123456789ABCDEF".toCharArray()

fun ByteArray.toHex(): String {
    val result = StringBuffer()

    forEach {
        val octet = it.toInt()
        val firstIndex = (octet and 0xF0).ushr(4)
        val secondIndex = octet and 0x0F
        result.append(HEX_CHARS[firstIndex])
        result.append(HEX_CHARS[secondIndex])
    }

    return result.toString()
}

fun String.hexStringToByteArray(): ByteArray {
    val result = ByteArray(length / 2)

    for (i in indices step 2) {
        val firstIndex = HEX_CHARS.indexOf(this[i])
        val secondIndex = HEX_CHARS.indexOf(this[i + 1])

        val octet = firstIndex.shl(4).or(secondIndex)
        result[i.shr(1)] = octet.toByte()
    }

    return result
}

fun fillByteArrayToFixedDimension(
    array: ByteArray,
    fixedSize: Int,
): ByteArray {
    if (array.size == fixedSize) {
        return array
    }

    val start = byteArrayOf(0x00.toByte())
    val filledArray = ByteArray(start.size + array.size)
    System.arraycopy(start, 0, filledArray, 0, start.size)
    System.arraycopy(array, 0, filledArray, start.size, array.size)
    return fillByteArrayToFixedDimension(filledArray, fixedSize)
}
