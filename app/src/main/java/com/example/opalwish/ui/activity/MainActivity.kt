package com.example.opalwish.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        com.example.opalwish.databinding.ActivityMainBinding.inflate(layoutInflater)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.getstartButton.setOnClickListener {
            startActivity(Intent(this@MainActivity, HomeActivity::class.java))
            finish()
        }
    }

}