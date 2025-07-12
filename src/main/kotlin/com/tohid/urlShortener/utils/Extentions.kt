package com.tohid.urlShortener.utils

private const val BASE62_CHARSET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"

fun Long.toBase62(): String {
    require(this >= 0) { "Cannot convert negative numbers to Base62: $this" }
    var num = this
    val base = BASE62_CHARSET.length
    val result = StringBuilder()

    while (num > 0) {
        result.append(BASE62_CHARSET[(num % base).toInt()])
        num /= base
    }

    return result.reverse().toString().padStart(8, '0')
}
