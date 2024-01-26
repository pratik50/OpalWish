package com.example.opalwish

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import coil.load
import com.example.opalwish.databinding.ActivityDetailBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class DetailActivity : AppCompatActivity() {

    val binding by lazy {
        ActivityDetailBinding.inflate(layoutInflater)
    }
    var productModel = ProductModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val productId = intent.getStringExtra("PRODUCT_ID")

        if(productId != null) {
            Firebase.firestore.collection("Products").document(productId!!).get()
                .addOnSuccessListener {

                    productModel = it.toObject<ProductModel>()!!
                    productModel.id = it.id
                    binding.productImage.load(productModel.imageUrl)

                }
            }else{
            Toast.makeText(this, "Occurring some issue in backend !!", Toast.LENGTH_SHORT).show()
        }

        binding.buyNow.setOnClickListener {
            startActivity(Intent(this@DetailActivity,ShippingActivity::class.java))
        }
    }
}