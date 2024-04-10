package com.example.opalwish.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.example.opalwish.R
import com.example.opalwish.adapters.OfferSliderAdapter
import com.example.opalwish.adapters.ProductAdapter
import com.example.opalwish.data.ProductModel
import com.example.opalwish.databinding.FragmentDashboardBinding
import com.example.opalwish.ui.activity.ProductCategoryActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlin.math.abs

class DashboardFragment : Fragment() {

    private lateinit var binding: FragmentDashboardBinding
    private lateinit var productList: ArrayList<ProductModel>
    private lateinit var adapter: ProductAdapter
    private lateinit var viewPager: ViewPager2
    private lateinit var sliderAdapter: OfferSliderAdapter
    private lateinit var imageList: ArrayList<Int>
    private lateinit var handler: Handler


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDashboardBinding.inflate(inflater, container, false)

        searchItem()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        binding = FragmentDashboardBinding.bind(view)
        viewPager = binding.sliderViewpager

        // Initialize adapter
        productList = ArrayList()
        adapter = ProductAdapter(requireContext(), productList)
        binding.mainRv.adapter = adapter
        binding.mainRv2.adapter = adapter

        // Initialize other UI components and setup ViewPager2
        init()
        setUpTransformer()
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                handler.removeCallbacks(runnable)
                handler.postDelayed(runnable,3000)
            }
        })

        // Start shimmer effect
        Handler(Looper.getMainLooper()).postDelayed({
            startShimmer()
        }, 1500)

        // Fetching data after the shimmer starts
        Handler(Looper.getMainLooper()).postDelayed({
            fetchData()
        }, 2000)


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

    private fun startShimmer() {
        binding.apply {
            shimmerRv1.startShimmer()
            shimmerRv2.startShimmer()
            shimmerRv3.startShimmer()
            shimmerRv4.startShimmer()
            shimmerRv5.startShimmer()
            shimmerRv6.startShimmer()
        }
    }

    private fun fetchData() {
        Firebase.firestore.collection("Products").get().addOnSuccessListener { documents ->
            productList.clear()
            for (document in documents) {
                val product_id = document.id
                val tempProductModel = document.toObject<ProductModel>()
                tempProductModel.product_id = product_id
                productList.add(tempProductModel)
            }
            adapter.notifyDataSetChanged()

            // Hide shimmer and show RecyclerView
            binding.apply {
                shimmerRv1.visibility = View.GONE
                shimmerRv2.visibility = View.GONE
                shimmerRv3.visibility = View.GONE
                shimmerRv4.visibility = View.GONE
                shimmerRv5.visibility = View.GONE
                shimmerRv6.visibility = View.GONE

                mainRv.visibility = View.VISIBLE
                mainRv2.visibility = View.VISIBLE
            }

            // Check if the list is empty and update visibility of offer view
            if (productList.isNotEmpty()) {
                binding.offer.visibility = View.VISIBLE
            } else {
                binding.offer.visibility = View.GONE
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(requireContext(), exception.localizedMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private fun init(){
        handler = Handler(Looper.myLooper()!!)
        imageList = ArrayList()

        imageList.add(R.drawable.offer_sticker_slide1)
        imageList.add(R.drawable.offer_sticker_slide2)
        imageList.add(R.drawable.offer_sticker_slide3)

        val indicator = binding.indicator
        indicator.setViewPager(viewPager)

        sliderAdapter = OfferSliderAdapter(imageList,viewPager)
        viewPager.adapter = sliderAdapter


    }

    private fun setUpTransformer() {

        val transformer = CompositePageTransformer()
        transformer.addTransformer(MarginPageTransformer(40))
        transformer.addTransformer { page, position ->

            val r = 1 - abs(position)
            page.scaleY = 0.85f + r * 0.10f
        }

        viewPager.setPageTransformer(transformer)
    }

    private val runnable = Runnable {
        viewPager.currentItem += 1
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable)
    }

    override fun onResume() {
        super.onResume()
        viewPager.currentItem += 1
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

}