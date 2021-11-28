package com.ruzin.citybike.model

/**
 * Creating a [Constants] object for keeping the string values
 */
object Constants {
    const val BASE_URL = "https://api.citybik.es/v2/"
    const val API_ENDPOINT = "networks"
    const val CACHE_KEY = "cache_expiry"
    const val CACHE_MIN = 15
    const val CACHE_MAX = 60
    const val CACHE_CONTROL_HEADER = "Cache-Control"
    const val CACHE_CONTROL_VALUE = "public, max-age="
}