package com.example.opalwish.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.opalwish.R
import com.example.opalwish.databinding.RvCartItemBinding
import com.example.opalwish.room_database.AppDatabase
import com.example.opalwish.room_database.RoomProductModel
import com.example.opalwish.ui.activity.DetailActivity
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.log

class CartProductAdapter(var context: Context, private val cartItemList: MutableList<RoomProductModel>) :
    RecyclerView.Adapter<CartProductAdapter.CartItemViewHolder>() {


    class CartItemViewHolder(val binding: RvCartItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemViewHolder {
        val binding = RvCartItemBinding.inflate(LayoutInflater.from(context),parent,false)
        return CartItemViewHolder(binding)
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onBindViewHolder(holder: CartItemViewHolder, position: Int) {

        val dao = AppDatabase.getInstance(context).RoomDao()

        holder.binding.productImage.load(cartItemList[position].productImageUrl){
            placeholder(R.drawable.image_loader)
        }

        holder.binding.checkbox.isChecked = cartItemList[position].isSelected
        holder.binding.checkbox.setOnCheckedChangeListener { _, isChecked ->
            cartItemList[position].isSelected = isChecked

            GlobalScope.launch(Dispatchers.IO) {
                dao.updateProduct(cartItemList[position])
            }
        }
        

        holder.binding.clearProduct.setOnClickListener{
            GlobalScope.launch(Dispatchers.IO) {
                dao.deleteProduct(cartItemList[position].product_id)
            }

        }

        holder.binding.productImage.setOnClickListener {

            val productId = cartItemList[position].product_id

            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("PRODUCT_ID", productId)
            context.startActivity(intent)
        }

        holder.binding.productName.text = cartItemList[position].productName
        holder.binding.productPrice.text = "₹ " + cartItemList[position].productPrice.toString()
        holder.binding.productDisp.text = cartItemList[position].productDisp
    }

    override fun getItemCount(): Int {
        return cartItemList.size
    }

}
