package com.ruzin.citybike

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.ruzin.citybike.databinding.FragmentMapBinding
import com.ruzin.citybike.model.BikeCompany
import com.ruzin.citybike.viewmodel.FragmentViewModel
import com.ruzin.citybike.viewmodel.ViewModelFactory

/**
 * [Fragment] as the map screen of the application, showing the map location, pin and bike company name, city and country code
 */
class MapFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapBinding? = null
    //viewModel shared by the main activity and both fragments
    private val viewModel: FragmentViewModel by  activityViewModels { ViewModelFactory(requireContext()) }
    private val binding get() = _binding!!
    private var bikeCompany: BikeCompany? = null
    private lateinit var googleMap : GoogleMap
    private lateinit var mapMarker : Marker

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //getting the selected bike company
        bikeCompany = viewModel.selectedBikeCompany.value
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.fragment_map, bikeCompany?.name)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        //fragment has it's own options menu that differs from the Main activity and the other fragment
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        //hide the searchView when the map fragment is shown
        val searchItem: MenuItem = menu.findItem(R.id.search)
        searchItem.isVisible = false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //google map is a fragment itself, so it has to be invoked by the childFragmentManager
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMapReady(p0: GoogleMap) {
        googleMap = p0
        //setting up the bike company coordinates and zoom level
        val networkPosition = LatLng(bikeCompany?.location?.latitude!!, bikeCompany?.location?.longitude!!)
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(networkPosition, 13f)
        mapMarker = googleMap.addMarker(MarkerOptions()
            .position(networkPosition)
            .title(bikeCompany?.name) //using the default map info dialog for showing the bike company name and location
            .snippet("${bikeCompany?.location?.city}, ${bikeCompany?.location?.country}"))!!
        //animating the camera to the pin location
        googleMap.animateCamera(cameraUpdate)
    }
}