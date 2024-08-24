package com.meditrust.findadoctor

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.meditrust.findadoctor.databinding.ActivityMainBinding
import kotlinx.coroutines.tasks.await

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    // Declare the binding variable
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize the binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupRegistration()
    }

    private fun setupRegistration() {

        binding.btnRegister.setOnClickListener {
          //  createEmailRegistration()
        }


    }


}