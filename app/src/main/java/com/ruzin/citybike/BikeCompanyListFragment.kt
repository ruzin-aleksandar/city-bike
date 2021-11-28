package com.ruzin.citybike

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.ruzin.citybike.adapter.BikeCompanyAdapter
import com.ruzin.citybike.databinding.FragmentBikeCompaniesBinding
import com.ruzin.citybike.interfaces.RecyclerClickEventHandler
import com.ruzin.citybike.model.BikeCompany
import com.ruzin.citybike.viewmodel.FragmentViewModel
import com.ruzin.citybike.viewmodel.ViewModelFactory

/**
 * [Fragment] as the first screen of the application, showing the list of all bike companies received from the API.
 */

class BikeCompanyListFragment : Fragment(), RecyclerClickEventHandler {

    private var _binding: FragmentBikeCompaniesBinding? = null
    private val binding get() = _binding!!
    //viewModel shared by the main activity and both fragments
    private val viewModel: FragmentViewModel by activityViewModels { ViewModelFactory(requireContext()) }
    private var searchView : SearchView? = null
    private var searchItem : MenuItem? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBikeCompaniesBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = BikeCompanyAdapter(this, requireContext())
        binding.networksRecyclerView.adapter = adapter
        //setting up an observer for the loading of the API data
        viewModel.bikeCompanies.observe(viewLifecycleOwner, Observer {
            adapter.setCompanyList(it)
            if (binding.refreshControl.isRefreshing) {
                binding.refreshControl.isRefreshing = false
            }
        })
        // adding a swipe down to refresh the data on the RecyclerView
        binding.refreshControl.setOnRefreshListener {
            viewModel.getAllNetworks()
            if (searchItem!!.isActionViewExpanded) {
                searchItem?.collapseActionView()
            }
        }
        binding.refreshControl.isRefreshing = true
        viewModel.getAllNetworks()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //handling the selection of a row in the recyclerView
    override fun handleClick(bikeCompany: BikeCompany) {
        // saving the selected bike company in the view model
        viewModel.selectCompany(bikeCompany)
        //telling the navigation controller to navigate to the second fragment
        findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        searchItem = menu.findItem(R.id.search)
        //setting up the searchView on the actionbar
        if (searchItem != null) {
            searchView = searchItem?.actionView as SearchView
            searchView?.queryHint = getString(R.string.search_hint)
            searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    //hiding the keyboard when the text is submitted
                    searchView?.clearFocus()
                    //showing the filtered list on the recyclerView
                    viewModel.filterList(query!!)
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    //showing the filtered list on the recyclerView while the user is typing the search query
                    viewModel.filterList(newText!!)
                    return true
                }
            })
        }
    }

}


