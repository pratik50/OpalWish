package com.example.opalwish.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.opalwish.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: com.example.opalwish.databinding.ActivityHomeBinding
    private var i = 0;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = com.example.opalwish.databinding.ActivityHomeBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val bottomBar: BottomNavigationView = binding.bottomBar
        val navController = findNavController(R.id.nav_host_fragment_activity_home)
        bottomBar.setupWithNavController(navController)


        binding.bottomBar.setOnItemSelectedListener { it ->

            when (it.itemId) {
                R.id.dashboardFragment -> {
                    i = 0
                    navController.navigate(R.id.dashboardFragment)
                    true
                }
                R.id.wishListFragment -> {
                    i = 1
                    navController.navigate(R.id.wishListFragment)
                    true
                }
                R.id.cartFragment -> {
                    i = 2
                    navController.navigate(R.id.cartFragment)
                    true
                }
                R.id.profileFragment -> {
                    i = 3
                    navController.navigate(R.id.profileFragment)
                    true
                }
                else -> false
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (i == 0){
            finish()
        }
    }
}