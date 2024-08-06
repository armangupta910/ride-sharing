package com.example.ridesharing

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class enterLocation : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_location)

        findViewById<Button>(R.id.submit).setOnClickListener {
            val x = Intent(this,MainActivity::class.java)
            x.putExtra("picklat",findViewById<EditText>(R.id.picklat).text.toString())
            x.putExtra("picklong",findViewById<EditText>(R.id.picklong).text.toString())
            x.putExtra("droplat",findViewById<EditText>(R.id.droplat).text.toString())
            x.putExtra("droplong",findViewById<EditText>(R.id.droplong).text.toString())
            startActivity(x)
        }

    }
}