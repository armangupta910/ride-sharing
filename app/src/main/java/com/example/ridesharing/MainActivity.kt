package com.example.ridesharing

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.service.controls.ControlsProviderService.TAG
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import java.io.File
import kotlin.math.min

data class Driver(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val occupied: Boolean = true,
    val destlatitude: Double = 0.0,
    val destlongitude: Double = 0.0
)

class MainActivity : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationTextView: TextView
    private lateinit var distanceTextView: TextView
    private lateinit var mapView: MapView
    private val LOCATION_PERMISSION_REQUEST_CODE = 1

    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference

    private val apiKey = "5b3ce3597851110001cf6248629e6b57567944b0a52affcbd01a7fc6" // Replace with your OpenRouteService API key

    var picklat = 26.295352600685217
    var picklong = 73.03887320926604
    var droplat = 26.29831
    var droplong = 73.04564

    private val retrofitService: OpenRouteServiceApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.openrouteservice.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenRouteServiceApi::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



//        picklat = intent.getStringExtra("picklat")!!.toDouble()
//        picklong = intent.getStringExtra("picklong")!!.toDouble()
//        droplat = intent.getStringExtra("droplat")!!.toDouble()
//        droplong = intent.getStringExtra("droplong")!!.toDouble()



        // Initialize osmdroid with tile caching
        Configuration.getInstance().apply {
            val cacheDir = File(applicationContext.cacheDir, "osmdroid")
            if (!cacheDir.exists()) {
                cacheDir.mkdirs()
            }
            osmdroidBasePath = cacheDir
            osmdroidTileCache = cacheDir
            userAgentValue = BuildConfig.APPLICATION_ID
        }

        mapView = findViewById(R.id.map)
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)

        locationTextView = findViewById(R.id.locationTextView)
        distanceTextView = findViewById(R.id.distanceTextView) // Add this line to initialize distanceTextView



        // Initialize Location Services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000).build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location = locationResult.lastLocation
                location?.let {
                    updateMapLocation(it)
                }
            }
        }

        checkLocationPermission()

        // Define the spatial grid parameters
        val cellSize = 0.1 // Example cell size in degrees
        val minLat = 8.0
        val minLon = 68.0
        val maxLat = 37.0
        val maxLon = 97.0

        mapView = findViewById(R.id.map)
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)

        // Start fetching user location
        getUserLocationAndFetchDrivers()


        // Fetch and display drivers
    }

    private fun updateMapLocation(location: Location) {
//        val userLocation = GeoPoint(location.latitude, location.longitude)
        val userLocation = picklong?.toDouble()?.let { picklat?.toDouble()
            ?.let { it1 -> GeoPoint(it1, it) } }
        locationTextView.text = "Lat: ${location.latitude}, Lon: ${location.longitude}"



        Log.d("Location", "Lat: ${location.latitude}, Lon: ${location.longitude}")

        // Update map with user's location
        mapView.controller.setCenter(userLocation)
        mapView.controller.setZoom(17.0)

        // Add or update user marker
        val userMarker = Marker(mapView).apply {
            position = userLocation
            title = "User Location"
        }
        mapView.overlays.add(userMarker)

        Toast.makeText(this,"User set",Toast.LENGTH_SHORT).show()

        val destination = GeoPoint(droplong, droplat)

//        // Add or update destination marker
        val destMarker = Marker(mapView).apply {
            position = destination
            title = "Destination"
        }
        mapView.overlays.add(destMarker)
        Toast.makeText(this,"Destination set",Toast.LENGTH_SHORT).show()


//
//        // Get route from current location to destination
//        fetchRoute(userLocation, destination)
    }

    private fun getUserLocationAndFetchDrivers() {
        // Example location, replace this with actual user location fetching
        val userLocation = picklong?.toDouble()?.let { picklat?.toDouble()
            ?.let { it1 -> GeoPoint(it1, it) } }

        // Get cell index for the user's location
        val cellIndex = userLocation?.latitude?.let { getCellIndex(it, userLocation.longitude) }
        Toast.makeText(this,"Cell Index :- ${cellIndex}",Toast.LENGTH_SHORT).show()

        // Fetch drivers for the cell
        if (cellIndex != null) {
            fetchDriversForCell(cellIndex)
        }
    }

    private val MIN_LAT = 8.0
    private val MIN_LON = 68.0

    private fun getCellIndex(latitude: Double, longitude: Double): String {
        val latIndex = ((latitude - MIN_LAT) / Companion.CELL_SIZE).toInt()
        val lonIndex = ((longitude - MIN_LON) / Companion.CELL_SIZE).toInt()
        return "cell_${latIndex}_${lonIndex}"
    }

    private fun fetchDriversForCell(cellIndex: String) {
        val cellRef = database.child("cells").child(cellIndex).child("drivers")
        val userLocation = picklong?.toDouble()?.let { picklat?.toDouble()
            ?.let { it1 -> GeoPoint(it1, it) } }

        val destination = droplat?.toDouble()?.let { droplong?.toDouble()
            ?.let { it1 -> GeoPoint(it1, it) } }



        cellRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                mapView.overlays.clear() // Clear existing overlays
                var closestDriver: Driver? = null
                var minDistance = Double.MAX_VALUE
                val drivers = mutableListOf<Driver>()

                for (driverSnapshot in snapshot.children) {
                    val driverData = driverSnapshot.getValue(Driver::class.java)
                    driverData?.let { drivers.add(it) }
                }

                if (drivers.isEmpty()) {
                    Toast.makeText(this, "No drivers found in your cell.", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                val driverIterator = drivers.iterator()

                fun processNextDriver() {
                    if (driverIterator.hasNext()) {
                        val driver = driverIterator.next()
                        val driverLocation = GeoPoint(driver.latitude, driver.longitude)

                        if (userLocation != null) {
                            calculateDistance(userLocation, driverLocation, object : DistanceCallback {
                                @RequiresApi(Build.VERSION_CODES.R)
                                override fun onDistanceCalculated(distanceKm: Double) {
                                    runOnUiThread {
                                        Log.d(TAG, "Distance: $distanceKm")

                                        if (distanceKm < minDistance) {
                                            minDistance = distanceKm
                                            closestDriver = driver
                                        }

                                        addMarkerToMap(driverLocation, driver.occupied)

                                        // Process the next driver
                                        processNextDriver()
                                    }
                                }
                            })
                        }
                    } else {
                        // All drivers processed, draw the path to the closest driver
                        closestDriver?.let {
                            val closestDriverLocation = GeoPoint(it.latitude, it.longitude)
                            if (userLocation != null) {
                                fetchRoute(userLocation, closestDriverLocation)
                                if (destination != null) {
                                    fetchRoute1(closestDriverLocation,destination)
                                }
                            }
                        } ?: run {
                            Toast.makeText(this@MainActivity, "No drivers found in your cell.", Toast.LENGTH_SHORT).show()
                        }

                        mapView.invalidate()
                    }
                }

                // Start processing drivers
                processNextDriver()
            } else {
                Toast.makeText(this, "No drivers found in your cell.", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to fetch drivers: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }



    private fun drawPathOnMap(pathPoints: List<GeoPoint>) {
        val polyline = Polyline()
        polyline.outlinePaint.color = ContextCompat.getColor(this, R.color.black)
        polyline.outlinePaint.strokeWidth = 5f

        // Set path points
        polyline.setPoints(pathPoints)
        mapView.overlays.add(polyline)
    }

    interface DistanceCallback {
        fun onDistanceCalculated(distanceKm: Double)
    }

    private fun calculateDistance(start: GeoPoint, end: GeoPoint, callback: DistanceCallback) {
        val startStr = "${start.longitude},${start.latitude}"
        val endStr = "${end.longitude},${end.latitude}"

        retrofitService.getRoute(apiKey, startStr, endStr).enqueue(object : Callback<RouteResponse> {
            override fun onFailure(call: Call<RouteResponse>, t: Throwable) {
                t.printStackTrace()
                callback.onDistanceCalculated(0.0) // Handle error case
            }

            override fun onResponse(call: Call<RouteResponse>, response: Response<RouteResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { routeResponse ->
                        val distanceMeters = routeResponse.features[0].properties.segments[0].distance
                        val distanceKm = distanceMeters / 1000.0
                        callback.onDistanceCalculated(distanceKm)
                    } ?: run {
                        callback.onDistanceCalculated(0.0) // Handle case where response body is null
                    }
                } else {
                    callback.onDistanceCalculated(0.0) // Handle HTTP error
                }
            }
        })
    }



    private fun addMarkerToMap(location: GeoPoint, isOccupied: Boolean) {
        val marker = Marker(mapView)
        marker.position = location
        marker.title = if (isOccupied) "Taxi" else "Taxi"
//        marker.icon = if (isOccupied) {
//            ContextCompat.getDrawable(this, R.drawable.red) // Use your occupied taxi icon
//        } else {
//            ContextCompat.getDrawable(this, R.drawable.green) // Use your unoccupied taxi icon
//        }
        mapView.overlays.add(marker)
    }



    private fun fetchRoute(start: GeoPoint, end: GeoPoint): Double {
        val startStr = "${start.longitude},${start.latitude}"
        val endStr = "${end.longitude},${end.latitude}"

        var ans:Double = 0.0

        retrofitService.getRoute(apiKey, startStr, endStr).enqueue(object : Callback<RouteResponse> {
            override fun onFailure(call: Call<RouteResponse>, t: Throwable) {
                t.printStackTrace()
            }

            override fun onResponse(call: Call<RouteResponse>, response: Response<RouteResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { routeResponse ->
                        val coordinates = routeResponse.features[0].geometry.coordinates
                        val pathPoints = coordinates.map { GeoPoint(it[1], it[0]) }
                        runOnUiThread {
                            drawRoute(ArrayList(pathPoints))
                            // Display distance
                            val distanceMeters = routeResponse.features[0].properties.segments[0].distance
                            val distanceKm = distanceMeters / 1000.0
                            ans = distanceKm
                            distanceTextView.text = "Distance: %.2f km".format(distanceKm)

                        }
                    }
                }
            }
        })

        return ans
    }

    private fun fetchRoute1(start: GeoPoint, end: GeoPoint): Double {
        val startStr = "${start.longitude},${start.latitude}"
        val endStr = "${end.longitude},${end.latitude}"

        var ans:Double = 0.0

        retrofitService.getRoute(apiKey, startStr, endStr).enqueue(object : Callback<RouteResponse> {
            override fun onFailure(call: Call<RouteResponse>, t: Throwable) {
                t.printStackTrace()
            }

            override fun onResponse(call: Call<RouteResponse>, response: Response<RouteResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { routeResponse ->
                        val coordinates = routeResponse.features[0].geometry.coordinates
                        val pathPoints = coordinates.map { GeoPoint(it[1], it[0]) }
                        runOnUiThread {
                            drawRoute1(ArrayList(pathPoints))
                            // Display distance
                            val distanceMeters = routeResponse.features[0].properties.segments[0].distance
                            val distanceKm = distanceMeters / 1000.0
                            ans = distanceKm
                            distanceTextView.text = "Distance: %.2f km".format(distanceKm)

                        }
                    }
                }
            }
        })

        return ans
    }

    private fun drawRoute(path: ArrayList<GeoPoint>) {
        val routeOverlay = Polyline().apply {
            color = 0xFF0000FF.toInt() // Blue color
            setPoints(path)
        }

        // Add routeOverlay to mapView's overlays
        mapView.overlays.add(routeOverlay)
        mapView.invalidate() // Refresh map view to show the new overlay
    }

    private fun drawRoute1(path: ArrayList<GeoPoint>) {
        val routeOverlay = Polyline().apply {
            color = 0xFFFF0000.toInt() // Red color
            setPoints(path)
        }

        // Add routeOverlay to mapView's overlays
        mapView.overlays.add(routeOverlay)
        mapView.invalidate() // Refresh map view to show the new overlay
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            startLocationUpdates()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                startLocationUpdates()
            } else {
                // Permission denied, handle accordingly
            }
        }
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    companion object {
        private const val CELL_SIZE = 0.1
    }
}
