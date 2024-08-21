package com.example.android.politicalpreparedness.representative

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.BaseViewModel
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.launch

class RepresentativeViewModel(app: Application, private val state: SavedStateHandle) : BaseViewModel(app)  {
    private val _representatives: MutableLiveData<List<Representative>?>  by lazy {
        MutableLiveData<List<Representative>?>().apply {
            value = state["data"] ?: emptyList()
        }
    }
    val representatives: LiveData<List<Representative>?> = _representatives

    private val _address = MutableLiveData<Address>()
    val address: LiveData<Address>
        get() = _address

    private val _states = MutableLiveData<List<String>>()
    val states: LiveData<List<String>>
        get() = _states
    val stateSelected = MutableLiveData<Int>()

    init {
        _address.value = Address("", "", "", "", "")
        _states.value = app.resources.getStringArray(R.array.states).toList()
    }

    fun setDataState(data: List<Representative>?) {
        state["data"] = data
    }

    fun refreshRepresentatives() {
        viewModelScope.launch {
            try {
                _address.value!!.state = getSelectedState(stateSelected.value!!)
                val addressStr = address.value!!.toFormattedString()
                val response = CivicsApi.retrofitService.getRepresentatives(addressStr)
                val representativeList = response.offices.flatMap { office ->
                    office.getRepresentatives(response.officials)
                }
                _representatives.postValue(representativeList as MutableList<Representative>?)

            } catch (e: Exception) {
                e.printStackTrace()
                showSnackBarInt.postValue(R.string.no_network_or_address_not_found_msg)
            }
        }
    }

    private fun getSelectedState(stateIndex: Int): String {
        return states.value!!.toList()[stateIndex]
    }

    fun refreshByCurrentLocation(address: Address) {
        val stateIndex = _states.value?.indexOf(address.state)
        if (stateIndex != null && stateIndex >= 0) {
            stateSelected.value = stateIndex!!
            _address.value = address
            refreshRepresentatives()
        } else {
            showSnackBarInt.value = R.string.current_location_is_not_us_msg
        }
    }

    fun setAddress(address: Address){
        _address.value = address
    }

    fun setStateSelected(state:Int){
        stateSelected.value = state
    }

}
