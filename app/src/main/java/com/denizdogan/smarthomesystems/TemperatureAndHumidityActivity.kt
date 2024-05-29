package com.denizdogan.smarthomesystems

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.denizdogan.smarthomesystems.databinding.ActivityTemperatureAndHumidityBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class TemperatureAndHumidityActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTemperatureAndHumidityBinding

    private lateinit var database: FirebaseDatabase

    private lateinit var reference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTemperatureAndHumidityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()
        reference = database.getReference("goingdata")
    }

    private fun initializeOfTextViews() {
        reference.child("humidity").get().addOnSuccessListener {
            val humidity = KeyManager.privateKey?.let { it1 ->
                KeyManager.decryptWithPrivateKey(it.value.toString(), it1)
            }
            Log.i("firebase", "Got value $humidity")
            val updatedText = "Humidity : $humidity"
            binding.humidity.text = updatedText
        }.addOnFailureListener {
            Log.e("firebase", "Error getting data", it)
        }

        reference.child("temperature").get().addOnSuccessListener {
            val temperature = KeyManager.privateKey?.let { it1 ->
                KeyManager.decryptWithPrivateKey(it.value.toString(), it1)
            }
            Log.i("firebase", "Got value $temperature")
            val updatedText = "Temperature : $temperature"
            binding.temperature.text = updatedText
        }.addOnFailureListener {
            Log.e("firebase", "Error getting data", it)
        }
    }

    override fun onStart() {
        initializeOfTextViews()
        super.onStart()
    }


}