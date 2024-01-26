package com.example.opalwish

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.opalwish.databinding.ActivityMainBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val auth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()

        runOnUiThread{
            binding.getstartButton.setOnClickListener {
                if (auth.currentUser == null) {
                    // User is not authenticated, so navigate to the sign-up/login activity
                    startActivity(Intent(this@MainActivity, SignUpActivity::class.java))
                } else {
                    // User is authenticated, so navigate to the home activity
                    startActivity(Intent(this@MainActivity, HomeActivity::class.java))
                }
                finish()
            }
        }
    }
}