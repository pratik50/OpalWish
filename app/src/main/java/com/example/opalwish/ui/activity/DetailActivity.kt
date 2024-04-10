package com.example.opalwish.ui.activity

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

        if (productId != null) {
            Firebase.firestore.collection("Products").document(productId).get()
                .addOnSuccessListener {
                        @Suppress("DEPRECATION")
                        productModel = it.toObject<ProductModel>()!!
                    productModel.product_id = it.id
                        val imageUrl = productModel.imageUrl
                        binding.productImage.load(imageUrl)

                        binding.productName.text = productModel.name
                        binding.productDesc.text = productModel.disp
                        binding.productDetails.text = productModel.details
                        binding.productPrice.text = productModel.price.toString()
                    }
                .addOnFailureListener { exception ->
                    // Handle failure
                    Toast.makeText(this, "Failed to get product: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }


        binding.btnBuyNow.setOnClickListener {
            startActivity(Intent(this@DetailActivity, ShippingActivity::class.java))
        }
    }
}