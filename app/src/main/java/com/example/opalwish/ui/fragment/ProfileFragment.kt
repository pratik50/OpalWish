package com.example.opalwish.ui.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.opalwish.data.UserModel
import com.example.opalwish.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater,container,false)

        val toolbar = binding.toolbar
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        val sharedPreferences = requireContext().getSharedPreferences("com.example.opalwish.usersDetail", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        binding.rbMale.setOnClickListener {

            Toast.makeText(this.context, "Changes Saved", Toast.LENGTH_SHORT).show()
            editor.putBoolean("rbMale",true)
            editor.putBoolean("rbOther",false)
            editor.putBoolean("rbFemale",false)
            
            editor.apply()
        }
        binding.rbFemale.setOnClickListener {
            Toast.makeText(this.context, "Changes Saved", Toast.LENGTH_SHORT).show()
            editor.putBoolean("rbFemale",true)
            editor.putBoolean("rbMale",false)
            editor.putBoolean("rbOther",false)

            editor.apply()
        }
        binding.rbOther.setOnClickListener {
            Toast.makeText(this.context, "Changes Saved", Toast.LENGTH_SHORT).show()
            editor.putBoolean("rbOther",true)
            editor.putBoolean("rbMale",false)
            editor.putBoolean("rbFemale",false)

            editor.apply()
        }

        Log.d("qwer", "onCreateView: outside of the lifecycleScope")
        lifecycleScope.launch(Dispatchers.IO) {

            Log.d("inside", "onCreateView: inside lifecycle scope")
            try {
                Log.d("try", "onCreateView: insde try block")
                val userId = FirebaseAuth.getInstance().currentUser?.uid
                if(userId != null){
                    val documentSnapshot = FirebaseDatabase.getInstance().getReference("Users").child(userId).get().await()
                    if(documentSnapshot.exists()){
                        val mobile = documentSnapshot.child("mobile").value.toString()
                        val editor = sharedPreferences.edit()
                        Log.d("poiu", "onCreateView: $mobile")
                        editor.putString("userMobile",mobile)
                        editor.apply()
                    }
                }else{
                    Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
                }
            }catch (e: Exception){
                Toast.makeText(requireContext(), e.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        }

        val sharedPref = requireContext().getSharedPreferences("com.example.opalwish.usersDetail", Context.MODE_PRIVATE)
        val usrName = sharedPref.getString("userFirstName", "")
        val usrLastName = sharedPref.getString("userLastName", "")

        val usrEmail = sharedPref.getString("userEmail", "")
        val usrAddress = sharedPref.getString("userAddress", "")
        val usrPass = sharedPref.getString("userPassword", "")
        val usrMobile = sharedPref.getString("userMobile", "")

        val maleGender = sharedPref.getBoolean("rbMale",false)
        val femaleGender = sharedPref.getBoolean("rbFemale",false)
        val otherGender = sharedPref.getBoolean("rbOther",false)

        val fullName = "$usrName $usrLastName"
        binding.etName.setText(fullName)
        binding.etEmail.setText(usrEmail)
        binding.etMobile.setText(usrMobile)
        binding.etLocation.setText(usrAddress)
        binding.rbMale.setChecked(maleGender)
        binding.rbFemale.setChecked(femaleGender)
        binding.rbOther.setChecked(otherGender)

        setFieldsEditable(false)

        binding.btnSave.setOnClickListener {

            binding.btnSave.startAnimation()
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            val name = binding.etName.text.toString()
            val email = binding.etEmail.text.toString()
            val mobile = binding.etMobile.text.toString()
            val address = binding.etLocation.text.toString()

            val nameParts = name.trim().split(" ", limit = 2)
            val firstName = nameParts.getOrElse(0) { "" }
            val lastName = nameParts.getOrElse(1) { "" }

            sharedPref.edit()
                .putString("userFirstName", firstName)
                .putString("userLastName", lastName)
                .putString("userEmail", email)
                .putString("userMobile", mobile)
                .putString("userAddress", address)
                .apply()


            val addressData = mapOf(
                "address" to address.toString(),
            )

            val userData = mapOf(
                "email" to email.toString(),
                "firstName" to firstName,
                "lastName" to lastName.toString(),
                "mobile" to mobile.toString(),
                "password" to usrPass.toString()
            )


                if (userId != null){

                    val userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId)
                    userRef.updateChildren(userData)
                    val userAddressRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("address")
                    userAddressRef.updateChildren(addressData)
                        .addOnSuccessListener {
                            Toast.makeText(this.context, "Changes Saved", Toast.LENGTH_SHORT).show()
                            binding.btnSave.revertAnimation()
                            setFieldsEditable(false)
                        }
                        .addOnFailureListener {
                            Toast.makeText(this.context, "Failed to save changes", Toast.LENGTH_SHORT).show()
                            binding.btnSave.revertAnimation()
                            setFieldsEditable(false)
                        }
                }
        }

        // Edit Profile button click listener
        binding.editProfileBtn.setOnClickListener {
            setFieldsEditable(true)
            Toast.makeText(this.context, "Fields are now editable", Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }

    // Helper function to make the fields editable/non-editable
    private fun setFieldsEditable(isEditable: Boolean) {
        // Name field
        binding.etName.isFocusable = isEditable
        binding.etName.isFocusableInTouchMode = isEditable
        binding.etName.isClickable = isEditable

        // Email field
        binding.etEmail.isFocusable = isEditable
        binding.etEmail.isFocusableInTouchMode = isEditable
        binding.etEmail.isClickable = isEditable

        // Mobile number field
        binding.etMobile.isFocusable = isEditable
        binding.etMobile.isFocusableInTouchMode = isEditable
        binding.etMobile.isClickable = isEditable

        // Address field
        binding.etLocation.isFocusable = isEditable
        binding.etLocation.isFocusableInTouchMode = isEditable
        binding.etLocation.isClickable = isEditable
    }

}