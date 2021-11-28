package com.ruzin.citybike.interfaces

import com.ruzin.citybike.model.Constants
import com.ruzin.citybike.model.BikeCompanies
import retrofit2.Response
import retrofit2.http.GET

/**
 * [APIService] interface for creating the API request
 */
interface APIService {
    @GET(Constants.API_ENDPOINT)
    suspend fun getBikeCompanies(): Response<BikeCompanies>
}