package com.example.opalwish.ui.activity

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.opalwish.R
import com.github.leandroborgesferreira.loadingbutton.customViews.CircularProgressButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private val binding by lazy {
        com.example.opalwish.databinding.ActivityLoginBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.txtSignup.setOnClickListener {
            startActivity(Intent(this@LoginActivity, SignUpActivity::class.java))
        }

        //we have pre-initialized the "success popUp window" here for fast optimization
        val forgotPassDialog = Dialog(this@LoginActivity)
        forgotPassDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        forgotPassDialog.setCancelable(true)
        forgotPassDialog.setContentView(R.layout.forgot_pass_dialog)
        forgotPassDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.forgotPass.setOnClickListener {
            val verify: CircularProgressButton = forgotPassDialog.findViewById(R.id.verify_btn)
            val emailEditText: EditText = forgotPassDialog.findViewById(R.id.email)
            val success_text: TextView = forgotPassDialog.findViewById(R.id.success_text)
            val pass_text: TextView = forgotPassDialog.findViewById(R.id.pass_txt)
            forgotPassDialog.show()

            verify.setOnClickListener {

                verify.startAnimation()
                val email = emailEditText.text.toString().trim()
                if (email.isNotEmpty()) {

                    // Send the password reset email using Firebase
                    FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {

                                // Password reset email sent successfully
                                Toast.makeText(
                                    applicationContext,
                                    "Password reset email sent. Check your inbox.",
                                    Toast.LENGTH_LONG
                                ).show()
                                emailEditText.visibility = View.GONE
                                verify.visibility = View.GONE
                                pass_text.visibility = View.GONE
                                success_text.visibility = View.VISIBLE

                                // Dismiss the dialog after a delay of 5 sec
                                Handler(Looper.getMainLooper()).postDelayed({
                                    forgotPassDialog.dismiss()
                                },5000)
                            } else {
                                // If the email is not registered or other issues
                                verify.revertAnimation()
                                Toast.makeText(
                                    applicationContext,
                                    "Failed to send password reset email. Check your email address.",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                } else {
                    verify.revertAnimation()
                    Toast.makeText(
                        applicationContext,
                        "Enter your email address first.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        val dialog = Dialog(this@LoginActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.login_success_popup)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val done: Button = dialog.findViewById(R.id.done)

        binding.loginBtn.setOnClickListener {

            val email = binding.loginMail.text.toString().trim()
            val password = binding.loginPass.text.toString().trim()
            val firebase = FirebaseDatabase.getInstance().reference
            if (email.isNotEmpty() && password.isNotEmpty()) {
                binding.loginBtn.startAnimation()

                Firebase.auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {

                            val user = Firebase.auth.currentUser

                            //Retrieving the data from firebase to store in sharedPreference
                            user?.let { currentUser ->
                                firebase.child("Users").child(currentUser.uid)
                                    .addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            if (snapshot.exists()) {
                                                val userFirstName = snapshot.child("fistName").value.toString()
                                                val userLastName = snapshot.child("lastName").value.toString()
                                                val userEmail = snapshot.child("email").value.toString()
                                                val userPassword = snapshot.child("password").value.toString()
                                                sharedPreference(userFirstName, userLastName, userEmail, userPassword)
                                            } else {
                                                Toast.makeText(
                                                    this@LoginActivity,
                                                    "User data not found in the database",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }

                                        //storing the data sharedPreference
                                        private fun sharedPreference(
                                            userFirstName: String,
                                            userLastName: String,
                                            userEmail: String,
                                            userPassword: String
                                        ) {
                                            val sharedPref =
                                                getSharedPreferences("com.example.oplawish.usersDetail", Context.MODE_PRIVATE)
                                            val editor = sharedPref.edit()

                                            editor.putString("userFirstName", userFirstName)
                                            editor.putString("userLastName", userLastName)
                                            editor.putString("userEmail", userEmail)
                                            editor.putString("userPassword", userPassword)
                                            editor.apply()
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            Toast.makeText(
                                                this@LoginActivity,
                                                "Failed to fetch user data: ${error.message}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    })
                            }


                            binding.loginBtn.revertAnimation{
                                binding.loginBtn.text = "Logged In"
                            }
                            dialog.show()
                            // Sign in success, update UI with the signed-in user's information
                            if(user != null){
                                done.setOnClickListener {
                                    dialog.dismiss()
                                    startActivity(
                                        Intent(
                                            this@LoginActivity,
                                            MainActivity::class.java
                                        )
                                    )
                                    finish()
                                }
                            }else{
                                Toast.makeText(this, "Something went wrong!!", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            binding.loginBtn.revertAnimation{
                                binding.loginBtn.text = "login"
                            }
                            // If sign in fails, display a message to the user.
                            Toast.makeText(
                                baseContext,
                                "Authentication failed. Check your email and password.",
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                    }
            } else {
                // Handle empty email or password fields
                Toast.makeText(
                    baseContext,
                    "Email and password cannot be empty.",
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }
    }
}