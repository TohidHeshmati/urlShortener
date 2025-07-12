package com.tohid.urlShortener.utils

fun Long.toBase62(): String {
    val charset = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
    var num = this
    val base = charset.length
    val result = StringBuilder()

    while (num > 0) {
        result.append(charset[(num % base).toInt()])
        num /= base
    }

    return result.reverse().toString().padStart(8, '0')
}
