package com.example.opalwish.ui.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.opalwish.R
import com.example.opalwish.adapters.CartProductAdapter
import com.example.opalwish.databinding.FragmentCartBinding
import com.example.opalwish.network.RetrofitInstance
import com.example.opalwish.room_database.AppDatabase
import com.example.opalwish.room_database.RoomProductModel
import com.example.opalwish.ui.activity.CheckoutActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
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

        // Fetching address
        fetchAddress()

        //BottomSheet popUp Address conformation
        binding.changeAddress.setOnClickListener {
                val bottomSheet = BottomSheetDialog(requireContext(),R.style.BottomSheetStyle_)
                bottomSheet.setContentView(R.layout.address_item_bottomsheet)

                val pinCodeEditText = bottomSheet.findViewById<EditText>(R.id.add_pincode)
                val cityEditText = bottomSheet.findViewById<EditText>(R.id.add_city)
                val stateEditText = bottomSheet.findViewById<EditText>(R.id.add_state)
                val countryEditText = bottomSheet.findViewById<EditText>(R.id.add_country)
                val addressEditText = bottomSheet.findViewById<TextInputEditText>(R.id.add_address)
                val btnSaveAddress = bottomSheet.findViewById<com.github.leandroborgesferreira.loadingbutton.customViews.CircularProgressButton>(R.id.btn_save_address)

                btnSaveAddress?.setOnClickListener {
                    if (addressEditText?.text.isNullOrEmpty()) {
                        addressEditText?.error = "Address is required"
                    } else if (pinCodeEditText?.text.isNullOrEmpty()) {
                        pinCodeEditText?.error = "Pincode is required"
                    } else {
                        // Proceed with form submission

                        val addressData = mapOf(
                            "address" to addressEditText?.text.toString(),
                            "pincode" to pinCodeEditText?.text.toString(),
                        )
                        // Push a new address to the 'addresses' array field
                        val userId = FirebaseAuth.getInstance().currentUser?.uid
                        if (userId != null){
                            val userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("address")
                            // Set the address data under the user's UID
                            userRef.updateChildren(addressData)
                                .addOnSuccessListener {
                                    Log.d("AddressUpdate", "Address added successfully")
                                }
                                .addOnFailureListener { e ->
                                    Log.e("AddressUpdate", "Error adding address", e)
                                }
                                // Optionally, fetch the updated address (if needed)
                                fetchAddress()
                                btnSaveAddress.revertAnimation()
                                bottomSheet.dismiss()
                            }
                    }
                }

                pinCodeEditText?.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        val pincode = s.toString()
                        if (pincode.length == 6) {
                            fetchLocationDetails(pincode, cityEditText, stateEditText, countryEditText)
                        }
                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                })

                bottomSheet.show()
        }

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


    private fun fetchLocationDetails(
        pincode: String,
        cityEditText: EditText?,
        stateEditText: EditText?,
        countryEditText: EditText?
    ) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitInstance.api.getPincodeDetails(pincode)
                if (response.isSuccessful) {
                    val details = response.body()?.firstOrNull() // Get the first object in the list
                    if (details?.Status == "Success") {
                        val postOffice = details.PostOffice?.firstOrNull()
                        postOffice?.let {
                            withContext(Dispatchers.Main) {
                                cityEditText?.setText(it.District ?: "")
                                stateEditText?.setText(it.State ?: "")
                                countryEditText?.setText(it.Country ?: "")
                            }
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                requireContext(),
                                "Invalid pincode. Please try again.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            requireContext(),
                            "Error fetching details: ${response.errorBody()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        "Network error: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun fetchAddress() {
        val userID = Firebase.auth.currentUser?.uid
        if (userID != null){
            lifecycleScope.launch {
                try {
                    val address = Firebase.database.reference.child("Users").child(userID).child("address").child("address").get().await()
                    if (address.exists()){
                        binding.deliveryAddress.text = address.value.toString()
                        binding.changeAddress.text = "Change"
                    }else{
                        binding.changeAddress.text = "Add Address"
                    }
                }catch (e: Exception){
                    Toast.makeText(requireContext(), "$e", Toast.LENGTH_SHORT).show()
                }
            }
        }
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






