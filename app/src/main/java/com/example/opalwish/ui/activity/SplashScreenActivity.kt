package com.example.opalwish.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.opalwish.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SplashScreenActivity : AppCompatActivity() {

    private val auth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    override fun onStart() {
        super.onStart()

        runOnUiThread{
            Handler(Looper.getMainLooper()).postDelayed({
                if (auth.currentUser == null) {
                        // User is not authenticated, so navigate to the sign-up/login activity
                    startActivity(Intent(this@SplashScreenActivity, OnboardingActivity::class.java))
                    finish()
                } else {
                        // User is authenticated, so navigate to the home activity
                    startActivity(Intent(this@SplashScreenActivity, HomeActivity::class.java))
                    finish()
                } },3000)
        }
    }
}