package com.denizdogan.smarthomesystems

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import org.json.JSONObject
import java.math.BigInteger
import java.security.Key
import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import java.security.interfaces.RSAPrivateCrtKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.RSAPrivateCrtKeySpec
import java.security.spec.RSAPublicKeySpec
import javax.crypto.Cipher

class MenuActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth

    private lateinit var database: FirebaseDatabase

    private lateinit var reference: DatabaseReference

    private lateinit var publicKey : PublicKey

    private lateinit var privateKey: PrivateKey

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        auth = Firebase.auth
        database = FirebaseDatabase.getInstance()
        reference = database.getReference("comingdata")

        manageRSAKeys(this)


    }

    fun showTemperatureAndHumidity(view : View){
        var intent = Intent(applicationContext, TemperatureAndHumidityActivity::class.java)
        startActivity(intent)
    }


    fun turnOnLight(view : View) {
        var intent = Intent(applicationContext, LightActivity::class.java)
        startActivity(intent)
    }

    fun turnOnSongMode(view : View) {
        var intent = Intent(applicationContext, SongActivity::class.java)
        startActivity(intent)
    }

    fun goToDistancePage(view: View){
        var intent = Intent(applicationContext, DistanceActivity::class.java)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.options_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.log_out){
            auth.signOut()
            val intent = Intent(this@MenuActivity, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun manageRSAKeys(context: Context) {
        getKeyPairFromFirebase { existingKeyPair ->
            if (existingKeyPair != null) {
                privateKey = existingKeyPair.private
                publicKey = existingKeyPair.public
                KeyManager.privateKey = privateKey
                KeyManager.publicKey = publicKey
                println(privateKey)
                println(publicKey)
            } else {
                val newKeyPair = generateRSAKeyPair()
                saveKeyPairToFirebase(newKeyPair)
                privateKey = newKeyPair.private
                publicKey = newKeyPair.public
            }
        }
    }

    private fun getKeyPairFromFirebase(onResult: (KeyPair?) -> Unit) {

        val ref = database.getReference("keys")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val privateKeyString = snapshot.child("privateKey").getValue(String::class.java)
                val publicKeyString = snapshot.child("publicKey").getValue(String::class.java)

                if (privateKeyString != null && publicKeyString != null) {
                    try {
                        val privateKeyJson = JSONObject(privateKeyString)
                        val publicKeyJson = JSONObject(publicKeyString)

                        val modulus = BigInteger(privateKeyJson.getString("modulus"), 16)
                        val publicExponent = BigInteger(privateKeyJson.getString("publicExponent"), 16)
                        val privateExponent = BigInteger(privateKeyJson.getString("privateExponent"), 16)
                        val primeP = BigInteger(privateKeyJson.getString("primeP"), 16)
                        val primeQ = BigInteger(privateKeyJson.getString("primeQ"), 16)
                        val primeExponentP = BigInteger(privateKeyJson.getString("primeExponentP"), 16)
                        val primeExponentQ = BigInteger(privateKeyJson.getString("primeExponentQ"), 16)
                        val crtCoefficient = BigInteger(privateKeyJson.getString("crtCoefficient"), 16)

                        val privateKeySpec = RSAPrivateCrtKeySpec(
                            modulus, publicExponent, privateExponent,
                            primeP, primeQ, primeExponentP, primeExponentQ, crtCoefficient
                        )

                        val publicKeySpec = RSAPublicKeySpec(
                            modulus, publicExponent
                        )

                        val keyFactory = KeyFactory.getInstance("RSA")
                        val privateKey = keyFactory.generatePrivate(privateKeySpec)
                        val publicKey = keyFactory.generatePublic(publicKeySpec)

                        onResult(KeyPair(publicKey, privateKey))
                    } catch (e: Exception) {
                        e.printStackTrace()
                        onResult(null)
                    }
                } else {
                    onResult(null)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                onResult(null)
            }
        })
    }

    // Generating RSA Private & Public Keys
    private fun generateRSAKeyPair(): KeyPair {
        val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
        keyPairGenerator.initialize(2048)
        return keyPairGenerator.genKeyPair()
    }

    private fun saveKeyPairToFirebase(keyPair: KeyPair) {
        val privateKey = keyPair.private as RSAPrivateCrtKey
        val publicKey = keyPair.public as RSAPublicKey

        val privateKeyJson = JSONObject().apply {
            put("modulus", privateKey.modulus.toString(16))
            put("publicExponent", privateKey.publicExponent.toString(16))
            put("privateExponent", privateKey.privateExponent.toString(16))
            put("primeP", privateKey.primeP.toString(16))
            put("primeQ", privateKey.primeQ.toString(16))
            put("primeExponentP", privateKey.primeExponentP.toString(16))
            put("primeExponentQ", privateKey.primeExponentQ.toString(16))
            put("crtCoefficient", privateKey.crtCoefficient.toString(16))
        }

        val publicKeyJson = JSONObject().apply {
            put("modulus", publicKey.modulus.toString(16))
            put("publicExponent", publicKey.publicExponent.toString(16))
        }


        val ref = database.getReference("keys")

        ref.child("privateKey").setValue(privateKeyJson.toString())
        ref.child("publicKey").setValue(publicKeyJson.toString())
    }
}