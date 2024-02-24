package com.example.opalwish.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.opalwish.data.OnboardingItem
import com.example.opalwish.R
import com.example.opalwish.adapters.ViewPagerAdapter

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: com.example.opalwish.databinding.ActivityOnboardingBinding

    private val onboardingItems = listOf(
        OnboardingItem(
            R.drawable.onboard_image1,
            "Choose Products",
            "Explore our handpicked selection. From must-haves to unique finds, your ideal product awaits. Let's get started!"
        ),
        OnboardingItem(
            R.drawable.onboard_image2,
            "Make Payment",
            "Seamless transactions at your fingertips. Quick, secure, and Smooth transactions, just a tap away!"
        ),
        OnboardingItem(
            R.drawable.onboard_image3,
            "Get Your Order",
            "Swift shipping straight to your door. Your order arrives in no time, hassle-free. Get ready to enjoy your new purchase!"
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = com.example.opalwish.databinding.ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewPager: ViewPager2 = binding.viewPager
        val adapter = ViewPagerAdapter(
            onboardingItems.map { it.title },
            onboardingItems.map { it.detail },
            onboardingItems.map { it.imageResource }
        )
        viewPager.adapter = adapter

        val indicator = binding.indicator
        indicator.setViewPager(viewPager)

        // Set onClickListener for Next
        binding.nextBtn.setOnClickListener {
            if (viewPager.currentItem < adapter.itemCount - 1) {
                viewPager.setCurrentItem(viewPager.currentItem + 1, true)
            }
        }

        // Set up ViewPager's OnPageChangeCallback
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == adapter.itemCount - 1) {
                    // Hide Next button and show Get Started button on last slide
                    binding.nextBtn.visibility = View.GONE
                    binding.startBtn.visibility = View.VISIBLE
                } else {
                    // Show Next button and hide Get Started button on other slides
                    binding.nextBtn.visibility = View.VISIBLE
                    binding.startBtn.visibility = View.GONE
                }
            }
        })

        binding.skipBtn.setOnClickListener {
            startActivity(Intent(this@OnboardingActivity, SignUpActivity::class.java))
            finish()
        }

        binding.startBtn.setOnClickListener {
            startActivity(Intent(this@OnboardingActivity, SignUpActivity::class.java))
            finish()
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}