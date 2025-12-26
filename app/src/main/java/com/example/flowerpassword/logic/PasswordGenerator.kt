package com.example.flowerpassword.logic

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * FlowerPassword algorithm implementation.
 * Ported from the original JavaScript Mini Program.
 */
object PasswordGenerator {

    private const val STR1 = "snow"
    private const val STR2 = "kise"
    private const val STR3 = "sunlovesnow1990090127xykab"

    /**
     * Generates a password based on the keyword (memory password) and code (key/account).
     * @param keyword The user's memory password.
     * @param code The distinction code (e.g., "google", "facebook").
     * @return The generated 16-character password, or null if inputs are invalid.
     */
    fun generate(keyword: String, code: String): String? {
        if (keyword.isBlank() || code.isBlank()) {
            return null
        }

        val md5one = hmacMd5(key = code, data = keyword)
        val md5two = hmacMd5(key = STR1, data = md5one)
        val md5three = hmacMd5(key = STR2, data = md5one)

        val rule = md5three.toCharArray()
        val source = md5two.toCharArray()

        // Convert to uppercase based on rule
        for (i in 0 until 32) {
            if (!source[i].isDigit()) {
                if (STR3.contains(rule[i])) {
                    source[i] = source[i].uppercaseChar()
                }
            }
        }

        val pwd32 = String(source)
        val firstChar = pwd32[0]

        // Make sure first char is not a number
        return if (!firstChar.isDigit()) {
            pwd32.substring(0, 16)
        } else {
            "K" + pwd32.substring(1, 16)
        }
    }

    /**
     * Computes HMAC-MD5.
     * @param key The secret key.
     * @param data The data to hash.
     * @return The hex-encoded HMAC-MD5 digest.
     */
    private fun hmacMd5(key: String, data: String): String {
        val algorithm = "HmacMD5"
        val mac = Mac.getInstance(algorithm)
        val secretKeySpec = SecretKeySpec(key.toByteArray(Charsets.UTF_8), algorithm)
        mac.init(secretKeySpec)
        val bytes = mac.doFinal(data.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
