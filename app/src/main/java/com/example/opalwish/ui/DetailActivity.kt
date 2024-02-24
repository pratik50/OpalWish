package com.example.opalwish.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.example.opalwish.data.ProductModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class DetailActivity : AppCompatActivity() {

    private val binding by lazy {
        com.example.opalwish.databinding.ActivityDetailBinding.inflate(layoutInflater)
    }
    private var productModel = ProductModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val productId = intent.getStringExtra("PRODUCT_ID")

        if(productId != null) {
            Firebase.firestore.collection("Products").document(productId).get()
                .addOnSuccessListener {

                    productModel = it.toObject<ProductModel>()!!
                    productModel.id = it.id
                    binding.productImage.load(productModel.imageUrl)

                }
            }else{
            Toast.makeText(this, "Occurring some issue in backend !!", Toast.LENGTH_SHORT).show()
        }

        binding.buyNow.setOnClickListener {
            startActivity(Intent(this@DetailActivity, ShippingActivity::class.java))
        }
    }
}