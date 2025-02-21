package com.example.opalwish.ui.activity

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.airbnb.lottie.LottieAnimationView
import com.example.opalwish.R
import com.example.opalwish.databinding.ActivityCheckoutBinding
import com.example.opalwish.room_database.RoomProductModel
import com.example.opalwish.ui.fragment.DashboardFragment
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import org.json.JSONObject

class CheckoutActivity : AppCompatActivity(), PaymentResultWithDataListener {

    private lateinit var binding: ActivityCheckoutBinding
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var orderConfirmDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //OrderPlaced Dialog Box Initialization
        orderConfirmDialog = Dialog(this@CheckoutActivity)
        orderConfirmDialog.setContentView(R.layout.order_place_popup)
        orderConfirmDialog.setCancelable(true)
        orderConfirmDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // Initialize Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar_checkout)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""

        val totalPrice = intent.getDoubleExtra("totalPrice", 0.0)
        binding.orderAmount.text = getString(R.string.total_amount, totalPrice)
        binding.totalPrice.text = getString(R.string.total_amount, totalPrice)

        val sharedPref = getSharedPreferences("com.example.opalwish.usersDetail", MODE_PRIVATE)
        val usrAddress = sharedPref.getString("userAddress", "")
        val userMobile = sharedPref.getString("userMobile", "")
        val usrEmail = sharedPref.getString("userEmail", "")

        binding.address.text = usrAddress

        Checkout.preload(applicationContext)
        val co = Checkout()
        co.setKeyID("rzp_test_ld3btyiWMEO1Nu")

        binding.PhonePe.setOnClickListener {
            initiatePayment(totalPrice*100, usrEmail, userMobile)
        }


      // Retrieve selected products
        val selectedProducts: ArrayList<RoomProductModel>? =
            intent.getParcelableArrayListExtra("SELECTED_PRODUCTS")

        selectedProducts?.let {
            // Handle the selected products (e.g., display them, calculate total, etc.)
            for (product in it) {
                Log.d("CheckoutActivity", "Product: ${product.productName}, Price: ${product.productPrice}")
            }
        }
    }


    fun initiatePayment(totalPrice: Double, usrEmail: String?, userMobile: String?) {
        val activity:Activity = this
        val co = Checkout()

        try {
            val options = JSONObject()
            options.put("name","OpalWish by pratik")
            options.put("description","Reference No. #123456")
            //You can omit the image option to fetch the image from the Dashboard
            options.put("image","http://example.com/image/rzp.jpg")
            options.put("theme.color", "#F68B8B");
            options.put("currency","INR");
            options.put("amount","$totalPrice") //pass amount in currency subunits

            options.put("method", "upi");

            val retryObj = JSONObject();
            retryObj.put("enabled", true);
            retryObj.put("max_count", 4);
            options.put("retry", retryObj);

            val prefill = JSONObject()
            prefill.put("email","$usrEmail")
            prefill.put("contact","$userMobile")

            options.put("prefill",prefill)
            co.open(activity,options)
            
        }catch (e: Exception){
            Toast.makeText(activity,"Error in payment: "+ e.message,Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    override fun onPaymentSuccess(p0: String?, p1: PaymentData?) {

        val lottie = orderConfirmDialog.findViewById<LottieAnimationView>(R.id.order_placed_animation)

        orderConfirmDialog.show()
        lottie.playAnimation()
        oderConfirmHandler()
        Toast.makeText(this, "Payment Success - $p0", Toast.LENGTH_LONG).show()
    }

    private fun oderConfirmHandler() {
        handler.postDelayed({
            runOnUiThread {
                if (orderConfirmDialog.isShowing) {
                    orderConfirmDialog.dismiss()
                }
                // Start HomeActivity and clear the back stack
                val intent = Intent(this, HomeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
        }, 6000)
    }

    override fun onPaymentError(p0: Int, p1: String?, p2: PaymentData?) {
        Toast.makeText(this, "Payment Failure - $p1", Toast.LENGTH_LONG).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}