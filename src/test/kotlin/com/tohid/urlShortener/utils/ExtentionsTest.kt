package com.tohid.urlShortener.utils;

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ExtensionsTest {

    private val base62Charset = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"

    @ParameterizedTest
    @ValueSource(longs = [0L, 1L, 10L, 61L, 62L, 12345L, 99999L, Long.MAX_VALUE])
    fun `toBase62 returns at least 8-character strings with valid base62 characters`(value: Long) {
        val base62 = value.toBase62()

        assertTrue(base62.length >= 8, "Expected at least 8 characters but got ${base62.length}")
        assertTrue(base62.all { it in base62Charset }, "Base62 contains invalid characters: $base62")
    }


    @ParameterizedTest
    @ValueSource(longs = [92233720368547758L, Long.MAX_VALUE])
    fun `toBase62 is longer than 8 characters for large numbers`(value: Long) {
        val base62 = value.toBase62()
        assertTrue(base62.length > 8, "Expected longer than 8 characters for large numbers")
    }

    @Test
    fun `toBase62 of zero returns correct padded value`() {
        val result = 0L.toBase62()
        assertEquals("00000000", result)
    }

    @Test
    fun `toBase62 throws exception for negative numbers`() {
        assertThrows<IllegalArgumentException> {
            (-1L).toBase62()
        }
    }

    @Test
    fun `toBase62 produces unique output for different inputs`() {
        val values = listOf(1L, 2L, 3L, 10L, 100L, 9999L)
        val results = values.map { it.toBase62() }.toSet()

        assertEquals(values.size, results.size)
    }
}
