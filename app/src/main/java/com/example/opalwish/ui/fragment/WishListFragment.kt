package com.example.opalwish.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.opalwish.adapters.ProductAdapter
import com.example.opalwish.data.ProductModel
import com.example.opalwish.databinding.FragmentWishListBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentWishListBinding.bind(view)

        productList = ArrayList()

        adapter = ProductAdapter(requireContext(), productList)
        binding.wishListRv.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.wishListRv.adapter = adapter

        firestore.collection("Products")
            .whereEqualTo("wishlist", true)
            .get()
            .addOnSuccessListener {
                productList.clear()

                for (i in it.documents) {

                    val tempProductModel = i.toObject<ProductModel>()
                    tempProductModel?.product_id = i.id

                    productList.add(tempProductModel!!)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.e("Error", "Failed to fetch wishlist products: ${exception.message}", exception)
                Toast.makeText(requireContext(), "Failed to fetch the products: ${exception.message}", Toast.LENGTH_SHORT).show()
            }

    }
}