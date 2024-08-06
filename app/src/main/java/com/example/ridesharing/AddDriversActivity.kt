package com.example.ridesharing

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AddDriversActivity : AppCompatActivity() {

    private lateinit var driverIdEditText: EditText
    private lateinit var latitudeEditText: EditText
    private lateinit var longitudeEditText: EditText
    private lateinit var addDriverButton: Button
    private lateinit var statusTextView: TextView

    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_drivers)

        driverIdEditText = findViewById(R.id.driverIdEditText)
        latitudeEditText = findViewById(R.id.latitudeEditText)
        longitudeEditText = findViewById(R.id.longitudeEditText)
        addDriverButton = findViewById(R.id.addDriverButton)
        statusTextView = findViewById(R.id.statusTextView)

        addDriverButton.setOnClickListener {
            addDriver()
        }
    }

    private fun addDriver() {
        val driverId = driverIdEditText.text.toString().trim()
        val latitude = latitudeEditText.text.toString().trim().toDoubleOrNull()
        val longitude = longitudeEditText.text.toString().trim().toDoubleOrNull()

        if (driverId.isEmpty() || latitude == null || longitude == null) {
            statusTextView.text = "Please enter all fields correctly."
            return
        }

        // Determine cell index based on latitude and longitude
        val cellIndex = getCellIndex(latitude, longitude)
        val driverRef = database.child("cells").child(cellIndex).child("drivers").child(driverId)

        val driverData = mapOf(
            "latitude" to latitude,
            "longitude" to longitude
        )

        driverRef.setValue(driverData)
            .addOnSuccessListener {
                Toast.makeText(this, "Driver added successfully.", Toast.LENGTH_SHORT).show()
                statusTextView.text = "Driver added successfully."
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to add driver: ${it.message}", Toast.LENGTH_SHORT).show()
                statusTextView.text = "Failed to add driver."
            }
    }

    private fun getCellIndex(latitude: Double, longitude: Double): String {
        val cellSize = 0.1 // Define cell size
        val minLat = 8.0
        val minLon = 68.0
        val latIndex = ((latitude - minLat) / cellSize).toInt()
        val lonIndex = ((longitude - minLon) / cellSize).toInt()
        return "cell_${latIndex}_${lonIndex}"
    }
}
