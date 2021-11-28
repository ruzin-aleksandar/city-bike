package com.ruzin.citybike.model

/**
 * [API] response model
 */
data class Location(
    val city: String,
    val country: String,
    val latitude: Double,
    val longitude: Double
)