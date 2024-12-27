package com.example.opalwish.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.opalwish.databinding.FragmentProfileBinding


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
        val sharedPref = requireContext().getSharedPreferences("com.example.opalwish.usersDetail", Context.MODE_PRIVATE)
        val usrName = sharedPref.getString("userFirstName", "")
        val usrLastName = sharedPref.getString("userLastName", "")
        val usrEmail = sharedPref.getString("userEmail", "")
        val usrPass = sharedPref.getString("userPassword", "")
        val maleGender = sharedPref.getBoolean("rbMale",false)
        val femaleGender = sharedPref.getBoolean("rbFemale",false)
        val otherGender = sharedPref.getBoolean("rbOther",false)

        val fullName = "$usrName $usrLastName"
        binding.etName.setText(fullName)
        binding.etEmail.setText(usrEmail)
        binding.etMobile.setText("Add mobile no.")
        binding.etLocation.setText("Add Address")
        binding.rbMale.setChecked(maleGender)
        binding.rbFemale.setChecked(femaleGender)
        binding.rbOther.setChecked(otherGender)

        setFieldsEditable(false)

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