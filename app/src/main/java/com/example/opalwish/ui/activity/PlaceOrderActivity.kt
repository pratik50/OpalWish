package com.example.opalwish.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.opalwish.R
import com.example.opalwish.adapters.PlaceOrderAdapter
import com.example.opalwish.databinding.ActivityPlaceOrderBinding
import com.example.opalwish.room_database.RoomProductModel

class PlaceOrderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlaceOrderBinding
    private lateinit var adapter: PlaceOrderAdapter
    private var cartItems = mutableListOf<RoomProductModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaceOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar_placeOrder)
        setSupportActionBar(toolbar)

        // Set back button (optional)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "CheckOut"


        // Initialize RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = PlaceOrderAdapter(this, cartItems)
        binding.recyclerView.adapter = adapter

        val totalPrice = intent.getDoubleExtra("totalPrice", 0.0)
        binding.orderTotal.text = getString(R.string.total_amount, totalPrice)
        binding.orderAmount.text = getString(R.string.total_amount, totalPrice)
        binding.finalPrice.text = getString(R.string.total_amount, totalPrice)

        // Get selected products from intent
        val selectedProducts = intent.getParcelableArrayListExtra<RoomProductModel>("SELECTED_PRODUCTS")

        if (selectedProducts.isNullOrEmpty()) {
            Log.d("emptyData", "No products selected")
        } else {
            cartItems.clear()
            cartItems.addAll(selectedProducts)
            adapter.updatePlaceOrderItems(selectedProducts)
        }




        // Checkout Button
        binding.checkoutButton.setOnClickListener {
            // Proceed to the next step (CheckoutActivity)
            if (cartItems.isNotEmpty()) {
                val intent = Intent(this, CheckoutActivity::class.java)
                intent.putParcelableArrayListExtra(
                    "SELECTED_PRODUCTS",
                    ArrayList(cartItems)
                )
                intent.putExtra("totalPrice", totalPrice)
                startActivity(intent)
            } else {
                Toast.makeText(this, "No items to checkout!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}