package com.example.opalwish.ui.activity

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.opalwish.R
import com.example.opalwish.databinding.ActivityCheckoutBinding
import com.example.opalwish.room_database.RoomProductModel
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import org.json.JSONObject

class CheckoutActivity : AppCompatActivity(), PaymentResultWithDataListener {

    private lateinit var binding: ActivityCheckoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar_checkout)
        setSupportActionBar(toolbar)

        // Set back button (optional)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Checkout"


        val totalPrice = intent.getDoubleExtra("totalPrice", 0.0)
        binding.orderAmount.text = getString(R.string.total_amount, totalPrice)
        binding.totalPrice.text = getString(R.string.total_amount, totalPrice)

        Checkout.preload(applicationContext)
        val co = Checkout()
        co.setKeyID("rzp_test_ld3btyiWMEO1Nu")

        binding.PhonePe.setOnClickListener {
            initiatePayment(totalPrice*100)
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


    fun initiatePayment(totalPrice: Double) {
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
            prefill.put("email","gaurav.kumar@example.com")
            prefill.put("contact","9404730294")

            options.put("prefill",prefill)
            co.open(activity,options)
            
        }catch (e: Exception){
            Toast.makeText(activity,"Error in payment: "+ e.message,Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    override fun onPaymentSuccess(p0: String?, p1: PaymentData?) {
        Toast.makeText(this, "$p0 Payment Success - $p1", Toast.LENGTH_LONG).show()
    }

    override fun onPaymentError(p0: Int, p1: String?, p2: PaymentData?) {
        Toast.makeText(this, "Payment Failure - $p1", Toast.LENGTH_LONG).show()
    }
}