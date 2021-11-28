package com.ruzin.citybike.viewmodel

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

/**
 * [ViewModelFactory] class used for passing the context parameter to the ViewModel.
 */

class ViewModelFactory constructor(private val context: Context): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(FragmentViewModel::class.java)) {
            FragmentViewModel(this.context) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}