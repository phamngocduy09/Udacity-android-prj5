package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.android.politicalpreparedness.BaseFragment
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import java.util.Locale

class DetailFragment : BaseFragment() {

    override val viewModel: RepresentativeViewModel by viewModels()
    private lateinit var requestLocationPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var enableLocationSettingLauncher: ActivityResultLauncher<IntentSenderRequest>
    private lateinit var binding: FragmentRepresentativeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRepresentativeBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        val adapter = RepresentativeListAdapter()
        binding.rcvRepresentative.adapter = adapter
        binding.buttonSearch.setOnClickListener {
            hideKeyboard()
            viewModel.refreshRepresentatives()
        }
        if (savedInstanceState != null) {
            viewModel.setDataState(viewModel.representatives.value)
            val address = Address(
                savedInstanceState.getString("line1", ""),
                savedInstanceState.getString("line2", ""),
                savedInstanceState.getString("city", ""),
                savedInstanceState.getString("state", ""),
                savedInstanceState.getString("zip", "")
            )
            viewModel.setAddress(address)
            viewModel.setStateSelected(savedInstanceState.getInt("selectedState", 0))
            binding.mlRepresentative.post {
                binding.mlRepresentative.setTransition(
                    savedInstanceState.getInt(
                        "motionLayoutStartState",
                        0
                    ), savedInstanceState.getInt("motionLayoutEndState", 0)
                )
                binding.mlRepresentative.progress =
                    savedInstanceState.getFloat("motionLayoutProgress", 0F)
            }
        }
        viewModel.representatives.observe(viewLifecycleOwner) { representatives ->
            adapter.submitList(representatives)
        }

        enableLocation()
        registerLocationPermissions()
        binding.buttonLocation.setOnClickListener { requestLocationPermissions() }
        return binding.root
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val stateSelected = viewModel.stateSelected.value
        val addressLine1 = viewModel.address.value?.line1
        val addressLine2 = viewModel.address.value?.line2
        val city = viewModel.address.value?.city
        val zip = viewModel.address.value?.zip
        val state = viewModel.address.value?.zip

        outState.putInt("selectedState", stateSelected ?: 0)
        outState.putString("line1", addressLine1)
        outState.putString("line2", addressLine2)
        outState.putString("city", city)
        outState.putString("zip", zip)
        outState.putString("state", state)
        outState.putInt("motionLayoutStartState", binding.mlRepresentative.startState)
        outState.putInt("motionLayoutEndState", binding.mlRepresentative.endState)
        outState.putFloat("motionLayoutProgress", binding.mlRepresentative.progress)

    }


    private fun enableLocation() {
        enableLocationSettingLauncher = registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { activityResult ->
            if (activityResult.resultCode == Activity.RESULT_OK)
                getLocation()
            else {
                viewModel.showSnackBarInt.value = R.string.location_required_error_msg
            }
        }
    }

    private fun requestLocationPermissions() {
        val isGranted = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (isGranted) {
            checkDeviceLocationSettingsAndGetLocation()
        } else {
            requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

    }

    private fun registerLocationPermissions() {
        requestLocationPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                checkDeviceLocationSettingsAndGetLocation()
            } else {
                viewModel.showSnackBarInt.value = R.string.no_location_permission_msg
            }
        }
    }

    private fun checkDeviceLocationSettingsAndGetLocation(resolve: Boolean = true) {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_LOW_POWER
        }
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val settingsClient = LocationServices.getSettingsClient(requireActivity())
        val locationSettingsResponseTask = settingsClient.checkLocationSettings(builder.build())

        locationSettingsResponseTask.addOnFailureListener { exception ->
            if (exception is ResolvableApiException && resolve) {
                try {

                    val intentSenderRequest =
                        IntentSenderRequest.Builder(exception.resolution).build()
                    enableLocationSettingLauncher.launch(intentSenderRequest)

                } catch (sendEx: IntentSender.SendIntentException) {
                    sendEx.printStackTrace()
                }
            } else {
                viewModel.showSnackBarInt.value = R.string.location_required_error_msg
            }
        }

        locationSettingsResponseTask.addOnCompleteListener {
            if (it.isSuccessful) {
                getLocation()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        val fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                locationResult.let {
                    val address = geoCodeLocation(it.lastLocation!!)
                    if (address != null) {
                        viewModel.refreshByCurrentLocation(address)
                    }
                    fusedLocationProviderClient.removeLocationUpdates(this)
                }
            }
        }

        val locationRequest = LocationRequest.create()
        Looper.myLooper()?.let {
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest, locationCallback,
                it
            )
        }
    }

    private fun geoCodeLocation(location: Location): Address? {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        return geocoder.getFromLocation(location.latitude, location.longitude, 1)
            ?.map { address ->
                Address(
                    address.thoroughfare,
                    address.subThoroughfare,
                    address.locality,
                    address.adminArea,
                    address.postalCode
                )
            }
            ?.first()
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }
}