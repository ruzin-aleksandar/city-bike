package com.ruzin.citybike.viewmodel

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ruzin.citybike.R
import com.ruzin.citybike.interfaces.APIService
import com.ruzin.citybike.model.Constants
import com.ruzin.citybike.model.BikeCompany
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.StringBuilder
import kotlin.collections.ArrayList

/**
 * [ViewModelFactory] class used for providing data to the fragments of the application
 */

class FragmentViewModel constructor(private val context: Context) : ViewModel() {

    var bikeCompanies = MutableLiveData<List<BikeCompany>>()
    private var allCompanies = ArrayList<BikeCompany>()
    var selectedBikeCompany = MutableLiveData<BikeCompany>()
    var searchQueryLength: Int = 0
    var cacheTimeout : Int = 0

    init {
        //getting the CacheTimeout value when the ViewModel is initialized
        getSelectedCacheTimeout()
    }

    fun getSelectedCacheTimeout() {
        //checking if there is a saved value for the cache timeout key, getting a default value of 15 if there isn't a saved value
        cacheTimeout = context.getSharedPreferences(context.getString(R.string.shared_preferences), Context.MODE_PRIVATE).getInt(Constants.CACHE_KEY, 15)
    }

    //keeping track of the selection, when a user clicks on a bike company on the RecyclerView
    fun selectCompany(bikeCompany: BikeCompany) {
        selectedBikeCompany.value = bikeCompany
    }

    // Search function
    fun filterList(searchString: String) {
        if (searchString.isEmpty()) {
            // if the search string is empty return the original list of bikeCompanies
            searchQueryLength = 0
            bikeCompanies.postValue(allCompanies)
        } else {
            // if the search query contains text
            if (searchString.length > searchQueryLength) {
                //if the new query is longer than the old, search within the previous, filtered results
                searchQueryLength = searchString.length
                bikeCompanies.postValue(performSearch(bikeCompanies.value!!, searchString))
            } else {
                //if the new query is shorter than the old, search from the original unfiltered results
                searchQueryLength = searchString.length
                bikeCompanies.postValue(performSearch(allCompanies, searchString))
            }
        }
    }

    private fun performSearch(list: List<BikeCompany>, query: String): List<BikeCompany> {
        //filter the provided list and create a new list that will contain only the bikeCompanies whose name contains the query string anywhere
        val filteredList = list.filter {
            it.name.contains(query, true)
        }
        return filteredList
    }


    /**
     * since [the CityBik API](http://api.citybik.es/v2/bikeCompanies) doesn't provide a cache control header, adding a response interceptor
     * that will add the [Cache-Control] policy to the response
     */
    fun getAllNetworks() {
        // setting up the cache size to 5 MB
        val cacheSize = (5 * 1024 * 1024).toLong()
        //setting up the cache directory
        val bikeCache = Cache(context!!.cacheDir, cacheSize)
        val okHttpClient = OkHttpClient.Builder()
            .cache(bikeCache)
            .addInterceptor { chain ->
                var request = chain.request()
                // check if internet connection is available
                request = if (isNetworkConnected(context)) {
                    // if internet is available create a normal request
                    request.newBuilder().build()
                } else {
                    // if internet is NOT available force a cache request
                    request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build()
                }
                chain.proceed(request)
            }
                //intercept the API response and ad the Cache-Control policy
            .addNetworkInterceptor(CacheInterceptor(cacheTimeout))
            .build()
        //build the request
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
        val retrofitService = retrofit.create(APIService::class.java)
        // using coroutines to make the API call on the IO thread
        CoroutineScope(Dispatchers.IO).launch {
            val response = retrofitService.getBikeCompanies()
            //post the received API values in the mutableLiveData on the main thread so it can update the UI
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    bikeCompanies.postValue(response.body()?.networks)
                    allCompanies.addAll(response.body()!!.networks)
                }
            }
        }
    }

    //check if internet is connected
    private fun isNetworkConnected(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        return networkCapabilities != null &&
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

}

//creating the bikeCompanies response interceptor for adding the Cache-Control policy
class CacheInterceptor constructor(timeout: Int) : Interceptor {

    private var timeout : Int  = 15

    //converting the cache timeout time to seconds
    init {
        this.timeout = timeout * 60
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        var response: Response = chain.proceed(chain.request())
        val cacheControl = response.header(Constants.CACHE_CONTROL_HEADER)
        // check if Cache-Control header exits in the response
        response = if (cacheControl == null) {
            //adding the Cache-Control string to the response header
            response.newBuilder().header(Constants.CACHE_CONTROL_HEADER, createCacheValue(timeout)).build()
        } else {
            response
        }
        return response
    }

    //creating the Cache-Control string
    private fun createCacheValue(timeout: Int) : String {
        val stringBuilder = StringBuilder()
        stringBuilder.append(Constants.CACHE_CONTROL_VALUE)
        stringBuilder.append(timeout)
        return stringBuilder.toString()
    }
}
