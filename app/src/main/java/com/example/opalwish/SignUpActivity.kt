package com.example.opalwish

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.Toast
import com.example.opalwish.databinding.ActivitySignUpBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivitySignUpBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //we have pre-initialized the "success popUp window" here for fast optimization
        val dialog = Dialog(this@SignUpActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.signup_success_popup)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val done: Button = dialog.findViewById(R.id.done)

        //Login page forward
        binding.txtLogin.setOnClickListener {
            startActivity(Intent(this@SignUpActivity,LoginActivity::class.java))
            finish()
        }
        //Login Success listener with nested "popUp window"
        binding.signUpBtn.setOnClickListener {
            Firebase.auth.createUserWithEmailAndPassword(
                binding.email.text.toString(),
                binding.createPass.text.toString()
            ).addOnCompleteListener { it ->

                if (it.isSuccessful) {
                    val userModel = UserModel(
                        binding.firstName.text.toString(),
                        binding.lastName.text.toString(),
                        binding.createPass.text.toString(),
                        binding.email.text.toString()
                    )

                    Firebase.database.reference.child("Users").child(it.result.user!!.uid)
                        .setValue(userModel).addOnCompleteListener {
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
                            Toast.makeText(
                                this@SignUpActivity,
                                it.localizedMessage,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                } else {
                    Toast.makeText(
                        this@SignUpActivity,
                        it.exception?.localizedMessage,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}