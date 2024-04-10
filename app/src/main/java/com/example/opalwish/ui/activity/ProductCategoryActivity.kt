package com.example.opalwish.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.opalwish.adapters.ProductAdapter
import com.example.opalwish.data.ProductModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class ProductCategoryActivity : AppCompatActivity() {

    private val binding by lazy {
        com.example.opalwish.databinding.ActivityProductCategoryBinding.inflate(layoutInflater)
    }
    private lateinit var adapter: ProductAdapter
    private lateinit var productList: ArrayList<ProductModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val arrayList = ArrayList<ProductModel>()
        productList = arrayList

        adapter = ProductAdapter(this@ProductCategoryActivity, productList)
        binding.rv.layoutManager = GridLayoutManager((this@ProductCategoryActivity),2)
        binding.rv.adapter = adapter

        if (intent.hasExtra("Category")) {
            val category = intent.getStringExtra("Category")
            Firebase.firestore.collection("Products").whereEqualTo("category", category).get()
                .addOnSuccessListener {
                    productList.clear()
                    for (i in it.documents) {

                        @Suppress("DEPRECATION")
                        val tempProductModel = i.toObject<ProductModel>()
                        tempProductModel?.product_id = i.id

                        productList.add(tempProductModel!!)

                    }
                    adapter.notifyDataSetChanged()

                }
        } else {

        }
    }
}