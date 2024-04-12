package com.example.opalwish.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.opalwish.adapters.CartProductAdapter
import com.example.opalwish.data.ProductModel
import com.example.opalwish.databinding.FragmentCartBinding
import com.example.opalwish.room_database.AppDatabase
import com.example.opalwish.room_database.RoomDao
import com.example.opalwish.room_database.RoomProductModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CartFragment : Fragment() {

    private lateinit var binding: FragmentCartBinding
    private lateinit var adapter: CartProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(inflater, container, false)

        adapter = CartProductAdapter(requireContext(), emptyList())
        binding.cartRv.adapter = adapter

        val preferences = requireContext().getSharedPreferences("info", AppCompatActivity.MODE_PRIVATE)

        val editor = preferences.edit()
        editor.putBoolean("isCart", false)
        editor.apply()

        val doa = AppDatabase.getInstance(requireContext()).RoomDao()


        doa.getAllProduct().observe(requireActivity()){

            if (it.isEmpty()){
                binding.emptyCartText.visibility = View.VISIBLE
            }
            binding.cartRv.layoutManager = LinearLayoutManager(requireContext())
            binding.cartRv.adapter = CartProductAdapter(requireContext(), it)

            adapter.notifyDataSetChanged()
        }

        return binding.root
    }
    private fun calculateTotalPrice(cartItemList: List<RoomProductModel>): Double {
        var totalPrice = 0.0
        for (item in cartItemList) {
            totalPrice += item.productPrice ?: 0.0
        }
        return totalPrice
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val Dao = AppDatabase.getInstance(requireContext()).RoomDao()

            // Calculate total price and log it
            Dao.getAllProduct().observe(viewLifecycleOwner) { cartItemList ->
                val totalPrice = calculateTotalPrice(cartItemList)

                binding.totalAmount.text = "₹ "+totalPrice.toString()
            }

    }

}
