package com.example.placesapi

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.placesapi.adapter.LocationsRecyclerViewAdapter
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.PlaceLikelihood
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var btnGetLocation: Button
    private lateinit var rvLocalsOfInterest: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var myAdapter: LocationsRecyclerViewAdapter
    private lateinit var localsOfInterest: ArrayList<String>
    private lateinit var placesClient: PlacesClient

    companion object {
        const val REQUEST_CODE_FLPERMISSION = 1
        const val API_KEY = "INSERT YOUR KEY HERE"
        private const val TAG: String = "TESTE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        initListeners()
        initPlacesAPI()
    }

    private fun initViews() {
        localsOfInterest = arrayListOf()
        localsOfInterest.add(0, "Points Of Interest")
        btnGetLocation = findViewById<View>(R.id.btnGetLocation) as Button

        initRecyclerView()
    }

    private fun initRecyclerView() {
        rvLocalsOfInterest = findViewById(R.id.localsOfInterestRV)
        layoutManager = LinearLayoutManager(this)

        val recyclerView: RecyclerView = rvLocalsOfInterest
        recyclerView.layoutManager = layoutManager

        myAdapter = LocationsRecyclerViewAdapter(this, localsOfInterest)
        recyclerView.adapter = myAdapter

        val dividerItemDecoration = DividerItemDecoration(recyclerView.context, (layoutManager as LinearLayoutManager).orientation)
        recyclerView.addItemDecoration(dividerItemDecoration)
    }

    private fun initListeners() {
        btnGetLocation.setOnClickListener(this)
    }

    private fun initPlacesAPI() {
        // Initialize the SDK
        Places.initialize(applicationContext, API_KEY)

        // Create a new PlacesClient instance
        placesClient = Places.createClient(this)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnGetLocation -> {
                clearLocals()
                getPlacesOfInterest()
            }
        }
    }

    private fun clearLocals() {
        localsOfInterest.clear()
        localsOfInterest.add(0, "Points Of Interest")
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun getPlacesOfInterest() {
        // Use fields to define the data types to return.
        val placeFields: List<Place.Field> = listOf(Place.Field.NAME, Place.Field.BUSINESS_STATUS)

        // Use the builder to create a FindCurrentPlaceRequest.
        val request: FindCurrentPlaceRequest = FindCurrentPlaceRequest.newInstance(placeFields)

        // Call findCurrentPlace and handle the response (first check that the user has granted permission).
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val placeResponse = placesClient.findCurrentPlace(request)
            placeResponse.addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    val response = task.result
                    var businessStatus: String
                    for (placeLikelihood: PlaceLikelihood in response?.placeLikelihoods ?: emptyList()) {
                        businessStatus = when (placeLikelihood.place.businessStatus.toString()) {
                            "OPERATIONAL" -> {
                                "Open"
                            }
                            "CLOSED_TEMPORARILY" -> {
                                "Closed Temporarily"
                            }
                            else -> {
                                "Closed"
                            }
                        }
                        localsOfInterest.add("${placeLikelihood.place.name}, $businessStatus")
                        //Log.i(TAG, "${placeLikelihood.place.name}: ${placeLikelihood.place.businessStatus} : ${placeLikelihood.likelihood}")
                    }
                    myAdapter = LocationsRecyclerViewAdapter(this, localsOfInterest)
                    rvLocalsOfInterest.adapter = myAdapter

                } else {
                    val exception = task.exception
                    if (exception is ApiException) {
                        Log.e(TAG, "Place not found: ${exception.statusCode}")
                    }
                }
            }
        } else {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE_FLPERMISSION)
        }
    }
}