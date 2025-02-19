package com.example.opalwish.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.opalwish.R
import com.example.opalwish.databinding.RvPlaceOrderItemBinding
import com.example.opalwish.room_database.RoomProductModel
import com.example.opalwish.ui.activity.DetailActivity

class PlaceOrderAdapter(val context: Context, private val placeOrderList: MutableList<RoomProductModel>) :
    RecyclerView.Adapter<PlaceOrderAdapter.PlaceOrderViewHolder>() {

    inner class PlaceOrderViewHolder(val binding: RvPlaceOrderItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceOrderViewHolder {
        val binding = RvPlaceOrderItemBinding.inflate(LayoutInflater.from(context),parent,false)
        return PlaceOrderViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return placeOrderList.size
    }

    override fun onBindViewHolder(holder: PlaceOrderViewHolder, position: Int) {
        holder.binding.productImage.load(placeOrderList[position].productImageUrl){
            placeholder(R.drawable.image_loader)
        }
        holder.binding.productName.text = placeOrderList[position].productName
        holder.binding.productPrice.text = "₹ " + placeOrderList[position].productPrice.toString()
        holder.binding.productDisp.text = placeOrderList[position].productDisp

        holder.binding.productImage.setOnClickListener {
            val productId = placeOrderList[position].product_id
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("PRODUCT_ID", productId)
            context.startActivity(intent)
        }
        }

    fun updatePlaceOrderItems(newPlaceOrderItems: List<RoomProductModel>) {
        placeOrderList.clear()
        placeOrderList.addAll(newPlaceOrderItems)
        notifyDataSetChanged()
    }
    }