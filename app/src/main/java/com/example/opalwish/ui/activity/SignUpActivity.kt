package com.example.opalwish.ui.activity

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.opalwish.R
import com.example.opalwish.data.UserModel
import com.github.leandroborgesferreira.loadingbutton.customViews.CircularProgressButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {

    private val binding by lazy {
        com.example.opalwish.databinding.ActivitySignUpBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val signUpBtn: CircularProgressButton = findViewById(R.id.signUp_btn)

        //we have pre-initialized the "success popUp window" here for fast optimization
        val dialog = Dialog(this@SignUpActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.signup_success_popup)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val done: Button = dialog.findViewById(R.id.done)

        //Login page forward
        binding.txtLogin.setOnClickListener {
            startActivity(Intent(this@SignUpActivity, LoginActivity::class.java))
        }

        //Login Success listener with nested "popUp window"
        binding.signUpBtn.setOnClickListener {
            signUpBtn.startAnimation()

            val email = binding.email.text.toString().trim()
            val password = binding.createPass.text.toString().trim()
            val passwordCheck = binding.confirmPass.text.toString().trim()
            val firstName = binding.firstName.text.toString().trim()
            val lastName = binding.lastName.text.toString().trim()

            //checking the data entered is not empty
            if (email.isEmpty() || password.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || passwordCheck.isEmpty()) {
                signUpBtn.revertAnimation {
                    signUpBtn.text = "Sign Up"
                }
                Toast.makeText(
                    this@SignUpActivity,
                    "Please fill in all fields",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            } else if (password != passwordCheck) {
                signUpBtn.revertAnimation {
                    signUpBtn.text = "Sign Up"
                }
                Toast.makeText(this, "Password doesn't matched", Toast.LENGTH_SHORT).show()
            } else {

                // Perform sign-up action
                Firebase.auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {

                            // Sign-up successful, save user data
                            val userModel = UserModel(firstName, lastName, password, email)
                            Firebase.database.reference.child("Users").child(task.result.user!!.uid)
                                .setValue(userModel)
                                .addOnCompleteListener {

                                    //storing the data sharedPreference
                                    sharedPreference(email, password, firstName, lastName);

                                    // Stop loading animation
                                    signUpBtn.revertAnimation {
                                        signUpBtn.text = "Signed In"
                                    }

                                    // Show success dialog
                                    dialog.show()
                                    done.setOnClickListener {
                                        dialog.dismiss()
                                        startActivity(
                                            Intent(
                                                this@SignUpActivity,
                                                MainActivity::class.java
                                            )
                                        )
                                        finish()
                                    }
                                }
                                .addOnFailureListener {
                                    signUpBtn.revertAnimation {
                                        signUpBtn.text = "Sign Up"
                                    }
                                    Toast.makeText(
                                        this@SignUpActivity,
                                        it.localizedMessage,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        } else {
                            signUpBtn.revertAnimation {
                                signUpBtn.text = "Sign Up"
                            }
                            Toast.makeText(
                                this@SignUpActivity,
                                task.exception?.localizedMessage ?: "Sign-up failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }
    }

    //storing the data in sharedPreferences
    private fun sharedPreference(
        email: String,
        password: String,
        firstName: String,
        lastName: String
    ) {

        val sharedPref =
            getSharedPreferences("com.example.oplawish.usersDetail", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()

        editor.putString("userFirstName", firstName)
        editor.putString("userLastName", lastName)
        editor.putString("userEmail", email)
        editor.putString("userPassword", password)
        editor.apply()
    }
}