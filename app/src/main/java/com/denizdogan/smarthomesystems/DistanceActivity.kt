package com.denizdogan.smarthomesystems

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.denizdogan.smarthomesystems.databinding.ActivityDistanceBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class DistanceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDistanceBinding

    private lateinit var database: FirebaseDatabase

    private lateinit var reference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDistanceBinding.inflate(layoutInflater)
        setContentView(binding.root)


        database = FirebaseDatabase.getInstance()
        reference = database.getReference("goingdata")
    }

    private fun initializeOfTextView() {
        reference.child("distance").get().addOnSuccessListener {
            val distance = KeyManager.privateKey?.let { it1 ->
                KeyManager.decryptWithPrivateKey(it.value.toString(), it1)
            }
            Log.i("firebase", "Got value $distance")
            val updatedText = "Distance : " + distance + "cm"
            binding.distance.text = updatedText
        }.addOnFailureListener {
            Log.e("firebase", "Error getting data", it)
        }
    }

    override fun onStart() {
        initializeOfTextView()
        super.onStart()
    }

}