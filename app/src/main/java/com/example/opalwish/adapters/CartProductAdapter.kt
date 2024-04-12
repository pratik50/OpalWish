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
import com.example.opalwish.room_database.RoomProductModel
import com.example.opalwish.ui.activity.DetailActivity

class CartProductAdapter(var context: Context, private val cartItemList: List<RoomProductModel>) :
    RecyclerView.Adapter<CartProductAdapter.CartItemViewHolder>() {

    class CartItemViewHolder(val binding: RvCartItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemViewHolder {
        val binding = RvCartItemBinding.inflate(LayoutInflater.from(context),parent,false)
        return CartItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartItemViewHolder, position: Int) {
        holder.binding.productImage.load(cartItemList[position].productImageUrl){
            placeholder(R.drawable.image_loader)
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
