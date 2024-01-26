package com.example.opalwish

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.opalwish.databinding.ActivityProductCategoryBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class ProductCategoryActivity : AppCompatActivity() {

    val binding by lazy {
        ActivityProductCategoryBinding.inflate(layoutInflater)
    }
    private lateinit var adapter: ProductAdapter
    private lateinit var productList: ArrayList<ProductModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val arrayList = ArrayList<ProductModel>()
        productList = arrayList

        adapter = ProductAdapter(this@ProductCategoryActivity, productList)
        binding.rv.layoutManager = LinearLayoutManager(this@ProductCategoryActivity)
        binding.rv.adapter = adapter

        if (intent.hasExtra("Category")) {
            val category = intent.getStringExtra("Category")
            Firebase.firestore.collection("Products").whereEqualTo("category", category).get()
                .addOnSuccessListener {
                    productList.clear()
                    for (i in it.documents) {

                        var tempProductModel = i.toObject<ProductModel>()
                        tempProductModel?.id = i.id

                        productList.add(tempProductModel!!)

                    }
                    adapter.notifyDataSetChanged()

                }
        } else {

        }
    }
}