package com.denizdogan.smarthomesystems

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.denizdogan.smarthomesystems.databinding.ActivityLightBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class LightActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLightBinding

    private lateinit var database: FirebaseDatabase

    private lateinit var reference: DatabaseReference

    private var state = - 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLightBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()
        reference = database.getReference("comingdata")

        reference.child("led").get().addOnSuccessListener {
            Log.i("firebase", "Got value ${it.value}")
            state = KeyManager.decryptWithPrivateKey(it.value.toString(), KeyManager.privateKey!!).toInt()
            changeImage()
        }.addOnFailureListener {
            Log.e("firebase", "Error getting data", it)
        }
    }

    fun changeLight(view : View) {
        reference.child("led").get().addOnSuccessListener {
            Log.i("firebase", "Got value ${it.value}")
            val stateOfLed = KeyManager.decryptWithPrivateKey(it.value.toString(), KeyManager.privateKey!!)
            if (stateOfLed.toInt() == 0){
                reference.child("led").setValue(KeyManager.encryptWithPublicKey("1", KeyManager.publicKey!!))
                state = 1
            }
            else{
                reference.child("led").setValue(KeyManager.encryptWithPublicKey("0", KeyManager.publicKey!!))
                state = 0
            }
            changeImage()
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }
    }

    private fun changeImage() {
        if(state == 0 ){
            binding.lightImage.setImageResource(R.drawable.baseline_flashlight_off_24)
        }
        else{
            binding.lightImage.setImageResource(R.drawable.baseline_flashlight_on_24)
        }
    }
}