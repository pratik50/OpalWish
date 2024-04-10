package com.example.opalwish.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import coil.load
import com.example.opalwish.R
import com.example.opalwish.data.ProductModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class DetailActivity : AppCompatActivity() {

    private val binding by lazy {
        com.example.opalwish.databinding.ActivityDetailBinding.inflate(layoutInflater)
    }
    private var productModel = ProductModel()
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val productId = intent.getStringExtra("PRODUCT_ID")
        binding.productImage.load(R.drawable.image_loader)

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
                        binding.productPrice.text = "₹ "+ productModel.price.toString()
                    }
                .addOnFailureListener { exception ->
                    // Handle failure
                    Toast.makeText(this, "Failed to get product: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }

        binding.btnBuyNow.setOnClickListener {
            startActivity(Intent(this@DetailActivity, ShippingActivity::class.java))
        }

        binding.btnAddToCart.setOnClickListener {
            binding.btnAddToCart.backgroundTintList = ContextCompat.getColorStateList(this, R.color.theme_color)

            Toast.makeText(this, "Item added to Cart", Toast.LENGTH_SHORT).show()
        }
        
        binding.wishlistBtn.setOnCheckedChangeListener { _, isChecked ->

            if(isChecked){
                Toast.makeText(this, "Item added to wishList", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this, "Item removed from WishList", Toast.LENGTH_SHORT).show()
            }
            
        }
        
        binding.followBtn.setOnClickListener {
            Toast.makeText(this, "Not yet Implemented", Toast.LENGTH_SHORT).show()
        }
    }
}