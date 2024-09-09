package com.example.opalwish.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import coil.load
import com.example.opalwish.R
import com.example.opalwish.data.ProductModel
import com.example.opalwish.room_database.AppDatabase
import com.example.opalwish.room_database.RoomDao
import com.example.opalwish.room_database.RoomProductModel
import com.google.ar.core.ArCoreApk
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
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

        binding.arButton.setOnClickListener{
            checkArAvailability()
        }

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

        binding.wishlistBtn.setOnClickListener {


                val userId = FirebaseAuth.getInstance().currentUser?.uid

                if (userId != null && productId != null) {
                    val userWishlistRef = Firebase.firestore.collection("Wishlists").document(userId)
                    val isChecked = binding.wishlistBtn.isChecked

                    userWishlistRef.get()
                        .addOnSuccessListener { _ ->
                            // If the button is checked, add the product to the wishlist
                            if (isChecked) {
                                // Set the product ID to true in the user's wishlist
                                userWishlistRef.set(
                                    mapOf(productId to true),
                                    SetOptions.merge()
                                ).addOnSuccessListener {
                                    Toast.makeText(this, "Added to Wishlist", Toast.LENGTH_SHORT).show()
                                }.addOnFailureListener { exception ->
                                    Toast.makeText(
                                        this,
                                        "Failed to add to Wishlist: ${exception.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    binding.wishlistBtn.isChecked = false // Revert state if failure
                                }
                            } else {
                                // Remove the product ID from the user's wishlist
                                userWishlistRef.update(productId, FieldValue.delete())
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            this,
                                            "Removed from Wishlist",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }.addOnFailureListener { exception ->
                                        Toast.makeText(
                                            this,
                                            "Failed to remove from Wishlist: ${exception.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        binding.wishlistBtn.isChecked = true // Revert state if failure
                                    }
                            }
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

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val productId = intent.getStringExtra("PRODUCT_ID")

        if (userId != null && productId != null) {
            val userWishlistRef = Firebase.firestore.collection("Wishlists").document(userId)

            userWishlistRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {

                        val isWishlisted = documentSnapshot.getBoolean(productId) == true

                        binding.wishlistBtn.isChecked = isWishlisted
                    } else {

                        binding.wishlistBtn.isChecked = false
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Failed to fetch wishlist status: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }


    //Ar checking availability

    private fun checkArAvailability() {
        lifecycleScope.launch {
            // Show loading indicator
            val result = withContext(Dispatchers.IO) {
                ArCoreApk.getInstance().checkAvailability(this@DetailActivity)
            }

            when (result) {
                ArCoreApk.Availability.SUPPORTED_INSTALLED -> {
                    startActivity(Intent(this@DetailActivity, ArViewActivity::class.java))
                }
                ArCoreApk.Availability.SUPPORTED_NOT_INSTALLED -> {
                    showAlertDialog("Please install ARCore.", true)
                }
                ArCoreApk.Availability.UNKNOWN_ERROR -> {

                    Toast.makeText(applicationContext, "Error checking AR availability.", Toast.LENGTH_SHORT).show()
                    Log.d("pratik11", "checkArAvailability: $result")
                }
                ArCoreApk.Availability.UNSUPPORTED_DEVICE_NOT_CAPABLE -> {
                    Log.e("ArViewActivity", "AR not supported on this device.")
                    showAlertDialog("AR not supported on this device.")
                }
                ArCoreApk.Availability.UNKNOWN_TIMED_OUT -> {
                    Log.e("ArViewActivity", "AR availability check timed out.")
                    Toast.makeText(applicationContext, "AR availability check timed out.", Toast.LENGTH_SHORT).show()
                    finish()
                }
                ArCoreApk.Availability.SUPPORTED_APK_TOO_OLD -> {
                    Log.e("ArViewActivity", "ARCore APK is too old.")
                    showAlertDialog("ARCore APK is too old. Please update ARCore.")
                }

                ArCoreApk.Availability.UNKNOWN_CHECKING -> {
                    checkArAvailability()
                }
            }
        }
    }


    private fun showAlertDialog(message: String, shouldRedirect: Boolean = false) {
        AlertDialog.Builder(this)
            .setTitle("AR Availability Issue")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                if (shouldRedirect) {
                    // Redirect to Play Store or handle accordingly
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.ar.core"))
                    startActivity(intent)
                }
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }

}