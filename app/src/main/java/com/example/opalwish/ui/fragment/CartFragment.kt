package com.example.opalwish.ui.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.opalwish.R
import com.example.opalwish.adapters.CartProductAdapter
import com.example.opalwish.databinding.FragmentCartBinding
import com.example.opalwish.room_database.AppDatabase
import com.example.opalwish.room_database.RoomProductModel
import com.example.opalwish.ui.activity.CheckoutActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CartFragment : Fragment() {

    private lateinit var binding: FragmentCartBinding
    private lateinit var adapter: CartProductAdapter

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(inflater, container, false)

        adapter = CartProductAdapter(requireContext(), emptyList())
        binding.cartRv.adapter = adapter

        binding.checkoutBtn.isEnabled = false

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

        binding.checkoutBtn.setOnClickListener {
            startActivity(Intent(requireContext(), CheckoutActivity::class.java))
        }

        return binding.root
    }

    private fun calculateTotalPrice(cartItemList: List<RoomProductModel>)  {
        var totalPrice = 0.0
        lifecycleScope.launch(Dispatchers.IO) {
            for (item in cartItemList) {
                if (item.isSelected) {
                    totalPrice += item.productPrice ?: 0.0
                }
            }
            withContext(Dispatchers.Main){
                binding.totalAmount.text = getString(R.string.total_amount, totalPrice)
                if(totalPrice > 0 ){
                    binding.checkoutBtn.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.theme_color)
                    binding.checkoutBtn.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.white))
                    binding.checkoutBtn.isEnabled = true
                }else{
                    binding.checkoutBtn.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.shimmer_color)
                    binding.checkoutBtn.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.black))
                    binding.checkoutBtn.isEnabled = false

                }
            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dao = AppDatabase.getInstance(requireContext()).RoomDao()

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            dao.getAllProduct().observe(viewLifecycleOwner) { cartItemList ->

                calculateTotalPrice(cartItemList)

            }
        }
    }
}






