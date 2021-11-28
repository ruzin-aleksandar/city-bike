package com.ruzin.citybike.interfaces

import com.ruzin.citybike.model.BikeCompany

/**
 * Handle the click on the [recyclerView] listing all bike companies
 */
interface RecyclerClickEventHandler {
    fun handleClick(bikeCompany: BikeCompany)
}