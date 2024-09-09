package com.example.opalwish.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.opalwish.adapters.ProductAdapter
import com.example.opalwish.data.ProductModel
import com.example.opalwish.databinding.FragmentWishListBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class WishListFragment : Fragment() {

    private lateinit var binding: FragmentWishListBinding
    private lateinit var adapter: ProductAdapter
    private lateinit var productList: ArrayList<ProductModel>

    private val firestore = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        binding = FragmentWishListBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        fetchWishlistProducts()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentWishListBinding.bind(view)

        productList = ArrayList()

        adapter = ProductAdapter(requireContext(), productList)
        binding.wishListRv.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.wishListRv.adapter = adapter

        fetchWishlistProducts()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fetchWishlistProducts() {
            val userId = FirebaseAuth.getInstance().currentUser?.uid

            if (userId != null) {
                lifecycleScope.launch {
                    try {
                        val documentSnapshot = firestore.collection("Wishlists").document(userId).get().await()

                        if (documentSnapshot.exists()) {
                            productList.clear()

                            for (field in documentSnapshot.data!!.keys) {
                                if (documentSnapshot.getBoolean(field) == true) {
                                    val productDocument = firestore.collection("Products").document(field).get().await()

                                    @Suppress("DEPRECATION")
                                    val product = productDocument.toObject<ProductModel>()
                                    product?.product_id = productDocument.id

                                    if (product != null && !productList.any { it.product_id == product.product_id }) {
                                        productList.add(product)
                                    }
                                }
                            }

                            adapter.notifyDataSetChanged()
                        } else {
                            Toast.makeText(requireContext(), "No items in wishlist", Toast.LENGTH_SHORT).show()
                        }
                    } catch (exception: Exception) {
                        Log.e("Error", "Failed to fetch the products: ${exception.message}", exception)
                        Toast.makeText(requireContext(), "Failed to fetch the products: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            }
        }
    }