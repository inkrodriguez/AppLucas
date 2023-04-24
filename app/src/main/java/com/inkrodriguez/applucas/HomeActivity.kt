package com.inkrodriguez.applucas

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.inkrodriguez.applucas.MainActivity.Companion.PREFS_KEY
import com.inkrodriguez.applucas.databinding.ActivityHomeBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var googleMap: GoogleMap
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onStart() {
        super.onStart()
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        val sharedPref = getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE)
        val email = sharedPref.getString("email", "")
        val password = sharedPref.getString("password", "")
        val btnTestCrashlytics = binding.btnTestCrashlytics

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)


        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)

        try {
            // código que pode gerar exceções
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }


        btnTestCrashlytics?.setOnClickListener {
            Toast.makeText(this, "$email e $password", Toast.LENGTH_SHORT).show()
        //throw RuntimeException("Test Crash") // Force a crash
        }

    }


    override fun onMapReady(map: GoogleMap) {

        val sharedPref = getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE)
        val email = sharedPref.getString("email", "")

        googleMap = map
        // Aqui você pode definir opções do mapa, como o tipo de mapa, zoom, etc.
        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL

        // Aqui você pode adicionar um marcador na localização atual do dispositivo
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val currentLocation = LatLng(location.latitude, location.longitude)
                    val markerOptions = MarkerOptions()
                        .position(currentLocation)
                        .title("My Location")
                    googleMap.addMarker(markerOptions)
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f))
                    
                    //Aqui eu pego a última localização do usuário
                    binding.latitude?.text = "Latitude: ${location.latitude}"
                    binding.longitude?.text = "Longitude: ${location.longitude}"

                    //Armazenar na base de dados local o usuário logado e sua última posição no mapa;
                    lifecycleScope.launch(Dispatchers.IO) {
                        updateLastLocation("${location.latitude}", "${location.longitude}")
                    }

                    // Aqui envio um evento de rastreamento de renderização
                    val params = Bundle()
                    params.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "map")
                    params.putString(FirebaseAnalytics.Param.ITEM_ID, "map_rendered")
                    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, params)
                }
            }
        }
    }



    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Companion.LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onMapReady(googleMap)
            }
        }
    }


    fun updateLastLocation(latitude: String, longitude: String){

        val sharedPref = getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE)
        val email = sharedPref.getString("email", "")

        AppDataBase(this).getUserDao().updateLastLocation(latitude, longitude, email.toString())

    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
    }

}