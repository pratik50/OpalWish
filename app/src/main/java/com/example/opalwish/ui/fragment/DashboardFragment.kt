package com.example.opalwish.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.opalwish.adapters.ProductAdapter
import com.example.opalwish.data.ProductModel
import com.example.opalwish.ui.activity.ProductCategoryActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class DashboardFragment : Fragment() {

    private lateinit var binding: com.example.opalwish.databinding.FragmentDashboardBinding
    private lateinit var productList: ArrayList<ProductModel>
    private lateinit var adapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = com.example.opalwish.databinding.FragmentDashboardBinding.inflate(
            inflater,
            container,
            false
        )

        searchItem()





        return binding.root
    }

    private fun searchItem() {
        val searchView = binding.searchBar
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        productList = ArrayList()
        adapter = ProductAdapter(requireContext(), productList)
        binding.mainRv.adapter = adapter

        Firebase.firestore.collection("Products").get().addOnSuccessListener {

            productList.clear()
            for (i in it.documents) {

                val tempProductModel = i.toObject<ProductModel>()
                productList.add(tempProductModel!!)

            }
            adapter.notifyDataSetChanged()
        }.addOnFailureListener{
            Toast.makeText(requireContext(), it.localizedMessage, Toast.LENGTH_SHORT).show()
        }


        binding.tshirt.setOnClickListener {
            startActivity(Intent(requireContext(), ProductCategoryActivity::class.java).putExtra("Category","T-Shirt"))
        }
        binding.shirt.setOnClickListener {
            startActivity(Intent(requireContext(), ProductCategoryActivity::class.java).putExtra("Category","Shirts"))
        }
        binding.mensBottom.setOnClickListener {
            startActivity(Intent(requireContext(), ProductCategoryActivity::class.java).putExtra("Category","Mens Bottom"))
        }
        binding.dress.setOnClickListener {
            startActivity(Intent(requireContext(), ProductCategoryActivity::class.java).putExtra("Category","Dress"))
        }
        binding.tops.setOnClickListener {
            startActivity(Intent(requireContext(), ProductCategoryActivity::class.java).putExtra("Category","Tops"))
        }
        binding.womensBottom.setOnClickListener {
            startActivity(Intent(requireContext(), ProductCategoryActivity::class.java).putExtra("Category","Women's Bottom"))
        }
        binding.babyDress.setOnClickListener {
            startActivity(Intent(requireContext(), ProductCategoryActivity::class.java).putExtra("Category","Baby Dress"))
        }
        binding.winter.setOnClickListener {
            startActivity(Intent(requireContext(), ProductCategoryActivity::class.java).putExtra("Category","Winters Collection"))
        }
        binding.caps.setOnClickListener {
            startActivity(Intent(requireContext(), ProductCategoryActivity::class.java).putExtra("Category","Caps"))
        }
    }

    companion object {

    }
}