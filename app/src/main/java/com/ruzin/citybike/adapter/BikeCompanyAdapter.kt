package com.ruzin.citybike.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ruzin.citybike.R
import com.ruzin.citybike.interfaces.RecyclerClickEventHandler
import com.ruzin.citybike.databinding.BikeCardBinding
import com.ruzin.citybike.model.BikeCompany
import com.squareup.picasso.Picasso

/**
 * [BikeCompanyAdapter] subclass of the RecyclerView.Adapter used to show the bike companies
 */
class BikeCompanyAdapter(private var clickListener: RecyclerClickEventHandler, private var context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var bikeCompanies = mutableListOf<BikeCompany>()

    //repopulate the RecyclerView each time the data changes
    fun setCompanyList(networks: List<BikeCompany>) {
        this.bikeCompanies = networks as MutableList<BikeCompany>
        notifyDataSetChanged()
    }

    class BikeViewHolder(var viewBinding: BikeCardBinding) : RecyclerView.ViewHolder(viewBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = BikeCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BikeViewHolder(binding)
    }

    //setting up the content of each cell in the recyclerView
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val bikeViewHolder = holder as BikeViewHolder
        val bikeCompany = bikeCompanies[position]
        bikeViewHolder.viewBinding.name.text = bikeCompany.name
        bikeViewHolder.viewBinding.cityCountry.text = context.getString(R.string.location_text, bikeCompany.location.city, bikeCompany.location.country)
        bikeViewHolder.viewBinding.cardView.setOnClickListener {
            clickListener.handleClick(bikeCompany)
        }
        /**
         * Using the Picasso library to download and cache flag images of the country that the bike company is in.
         * The flag image is downloaded from the Flagpedia free API by appending the bike company country code to the specified request.
         */
        Picasso.get().load(context.getString(R.string.flag_url, bikeCompany.location.country.toString().lowercase())).into(bikeViewHolder.viewBinding.flagImage);

    }

    override fun getItemCount(): Int {
        return bikeCompanies.size
    }
}