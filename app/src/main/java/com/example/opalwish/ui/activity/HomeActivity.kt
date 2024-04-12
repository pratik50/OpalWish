package com.example.opalwish.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.opalwish.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: com.example.opalwish.databinding.ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = com.example.opalwish.databinding.ActivityHomeBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val bottomBar: BottomNavigationView = binding.bottomBar
        val navController = findNavController(R.id.nav_host_fragment_activity_home)
        bottomBar.setupWithNavController(navController)


    }

}