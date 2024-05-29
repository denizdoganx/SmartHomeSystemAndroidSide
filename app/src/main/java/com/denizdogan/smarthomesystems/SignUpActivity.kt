package com.denizdogan.smarthomesystems

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.denizdogan.smarthomesystems.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding

    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

    }

    fun signUp(view : View){
        val email = binding.emailEt.text.toString()
        val password = binding.passET.text.toString()
        val confirmPassword = binding.confirmPassEt.text.toString()
        if(email == "" || password == "" || confirmPassword == ""){
            Toast.makeText(this@SignUpActivity, "Enter email and password!", Toast.LENGTH_LONG).show()
        }
        else if(password != confirmPassword){
            Toast.makeText(this@SignUpActivity, "Passwords don't match exactly.", Toast.LENGTH_LONG).show()
        }
        else{
            auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
                val intent = Intent(this@SignUpActivity, MenuActivity::class.java)
                startActivity(intent)
                finish()
            }.addOnFailureListener {
                Toast.makeText(this@SignUpActivity, it.localizedMessage, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun goToSignInPage(view :View){
        val intent = Intent(this@SignUpActivity, SignInActivity::class.java)
        startActivity(intent)
        finish()
    }
}