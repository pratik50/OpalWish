package com.example.opalwish.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import coil.load
import com.example.opalwish.R
import com.example.opalwish.data.ProductModel
import com.example.opalwish.room_database.AppDatabase
import com.example.opalwish.room_database.RoomDao
import com.example.opalwish.room_database.RoomProductModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

        binding.detailShimmer.startShimmer()
        binding.productImage.load(R.drawable.image_loader)

        checkWishlistStatus()

        val firestore = Firebase.firestore
        if (productId != null) {

            firestore.collection("Products").document(productId).get()
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

                    binding.detailShimmer.visibility = View.GONE
                    binding.detailInfo.visibility = View.VISIBLE
                    val name = productModel.name
                    val disp = productModel.disp
                    val price = productModel.price
                    if (imageUrl != null) {
                        cartAction(productId,name,disp,price,imageUrl)
                    }

                }
                .addOnFailureListener { exception ->
                    // Handle failure
                    Toast.makeText(this, "Failed to get product: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }

        binding.btnBuyNow.setOnClickListener {
            startActivity(Intent(this@DetailActivity, CheckoutActivity::class.java))
        }

        var previousState = productModel.wishlist

        binding.wishlistBtn.setOnCheckedChangeListener { _, isChecked ->

            if(productId != null){

                firestore.collection("Products").document(productId)
                    .update("wishlist", isChecked)
                    .addOnSuccessListener {

                        if (previousState != isChecked){
                            Toast.makeText(this, "Product added to wishlist", Toast.LENGTH_SHORT).show()

                        }else {
                            // Display toast message when the product is removed from the wishlist
                            Toast.makeText(this, "Product removed from wishlist", Toast.LENGTH_SHORT).show()
                        }

                    }.addOnFailureListener { exception ->
                        // Handle failure
                        Toast.makeText(this, "Failed to update wishlist status: ${exception.message}", Toast.LENGTH_SHORT).show()

                        binding.wishlistBtn.isChecked = !isChecked
                    }
            }
        }

        binding.followBtn.setOnClickListener {
            Toast.makeText(this, "Not yet Implemented", Toast.LENGTH_SHORT).show()
        }

    }

    private fun cartAction(productId: String, name: String?, disp: String?, price: Double?, image: String) {

        val dao = AppDatabase.getInstance(this).RoomDao()

        if(dao.isExit(productId) != null){
            binding.btnAddToCart.text = "Go To Cart"
        }else{
            binding.btnAddToCart.text = "Add to Cart"
        }

        binding.btnAddToCart.setOnClickListener {
            if(dao.isExit(productId) != null){
                openCart()
            }else{
                binding.btnAddToCart.backgroundTintList = ContextCompat.getColorStateList(this, R.color.theme_color)

                Toast.makeText(this, "Item added to Cart", Toast.LENGTH_SHORT).show()
                addToCart(dao,productId,name,disp,price,image)
            }
        }
    }

    private fun addToCart(roomDao: RoomDao, productId: String, name: String?, disp: String?, price: Double?, image: String) {

        val data = RoomProductModel(productId,name,price,disp,image)
        lifecycleScope.launch(Dispatchers.IO) {

            roomDao.insertProduct(data)

            withContext(Dispatchers.Main) {
                // Update UI here, such as setting text on a TextView
                binding.btnAddToCart.text = "Go to Cart"
            }
        }
    }

    private fun openCart() {

        val preferences = this.getSharedPreferences("info", MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putBoolean("isCart", true)
        editor.apply()

        startActivity(Intent(this,HomeActivity::class.java))
        finish()
    }

    private fun checkWishlistStatus() {

        val productId = intent.getStringExtra("PRODUCT_ID")

        productId?.let { id ->
            Firebase.firestore.collection("Products").document(id)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    val product = documentSnapshot.toObject<ProductModel>()
                    product?.let {
                        binding.wishlistBtn.isChecked = it.wishlist!!
                    }
                }
                .addOnFailureListener { exception ->
                    // Handle failure
                    Toast.makeText(this, "Failed to fetch product details: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}