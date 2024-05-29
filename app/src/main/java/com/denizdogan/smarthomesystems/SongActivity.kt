package com.denizdogan.smarthomesystems

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.denizdogan.smarthomesystems.databinding.ActivitySongBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SongActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySongBinding

    private lateinit var database: FirebaseDatabase

    private lateinit var reference: DatabaseReference

    private var songType = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySongBinding.inflate(layoutInflater)
        setContentView(binding.root)
        database = FirebaseDatabase.getInstance()
        reference = database.getReference("comingdata")
        binding.song1.isClickable = false
        binding.song1.isEnabled = false
        reference.child("song").child("type").get().addOnSuccessListener {
            Log.i("firebase", "Got value ${it.value}")
            songType = KeyManager.decryptWithPrivateKey(it.value.toString(), KeyManager.privateKey!!).toInt()
            if(songType == 1){
                binding.song1.isClickable = false
                binding.song1.isEnabled = false
                binding.song2.isClickable = true
                binding.song2.isEnabled = true
                binding.song3.isClickable = true
                binding.song3.isEnabled = true
            }
            else if(songType == 2){
                binding.song1.isClickable = true
                binding.song1.isEnabled = true
                binding.song2.isClickable = false
                binding.song2.isEnabled = false
                binding.song3.isClickable = true
                binding.song3.isEnabled = true
            }
            else{
                binding.song1.isClickable = true
                binding.song1.isEnabled = true
                binding.song2.isClickable = true
                binding.song2.isEnabled = true
                binding.song3.isClickable = false
                binding.song3.isEnabled = false
            }
        }.addOnFailureListener {
            Log.e("firebase", "Error getting data", it)
        }
    }

    fun play(view : View){
        reference.child("song").child("state").setValue(KeyManager.encryptWithPublicKey("1", KeyManager.publicKey!!))
    }

    fun pause(view : View){
        reference.child("song").child("state").setValue(KeyManager.encryptWithPublicKey("0", KeyManager.publicKey!!))
    }

    fun changeSong1(view : View){

        reference.child("song").child("type").setValue(KeyManager.encryptWithPublicKey("1", KeyManager.publicKey!!))

        binding.song1.isClickable = false
        binding.song1.isEnabled = false

        binding.song2.isClickable = true
        binding.song2.isEnabled = true

        binding.song3.isClickable = true
        binding.song3.isEnabled = true
    }

    fun changeSong2(view : View){

        reference.child("song").child("type").setValue(KeyManager.encryptWithPublicKey("2", KeyManager.publicKey!!))

        binding.song1.isClickable = true
        binding.song1.isEnabled = true

        binding.song2.isClickable = false
        binding.song2.isEnabled = false

        binding.song3.isClickable = true
        binding.song3.isEnabled = true
    }

    fun changeSong3(view : View){

        reference.child("song").child("type").setValue(KeyManager.encryptWithPublicKey("3", KeyManager.publicKey!!))

        binding.song1.isClickable = true
        binding.song1.isEnabled = true

        binding.song2.isClickable = true
        binding.song2.isEnabled = true

        binding.song3.isClickable = false
        binding.song3.isEnabled = false
    }
}