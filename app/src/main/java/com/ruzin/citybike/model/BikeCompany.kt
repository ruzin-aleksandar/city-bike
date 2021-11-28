package com.ruzin.citybike.model

import androidx.appcompat.app.AppCompatActivity

/**
 * [API] response model
 */
data class BikeCompany(
    val company: Any,
    val gbfs_href: String,
    val href: String,
    val id: String,
    val license: License,
    val location: Location,
    val name: String,
    val source: String
)