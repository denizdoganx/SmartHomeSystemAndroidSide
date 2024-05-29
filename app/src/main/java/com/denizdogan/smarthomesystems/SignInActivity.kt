package com.denizdogan.smarthomesystems

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.denizdogan.smarthomesystems.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignInActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySignInBinding

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        val currentUser = auth.currentUser

        if(currentUser != null){
            val intent = Intent(this@SignInActivity, MenuActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun signIn(view : View){
        val email = binding.emailEt.text.toString()
        val password = binding.passET.text.toString()


        if(email == "" || password == ""){
            Toast.makeText(this@SignInActivity, "Enter email and password!", Toast.LENGTH_LONG).show()
        }
        else{
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    val intent = Intent(this@SignInActivity, MenuActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this@SignInActivity, it.localizedMessage, Toast.LENGTH_LONG).show()
                }
        }
    }

    fun goToSignUpPage(view : View){
        val intent = Intent(this@SignInActivity, SignUpActivity::class.java)
        startActivity(intent)
        finish()
    }
}