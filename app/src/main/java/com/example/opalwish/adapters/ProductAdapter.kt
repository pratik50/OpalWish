package com.example.opalwish.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.opalwish.data.ProductModel
import com.example.opalwish.R
import com.example.opalwish.databinding.RvItemBinding
import com.example.opalwish.ui.activity.DetailActivity
import com.example.opalwish.ui.activity.ProductCategoryActivity

class ProductAdapter(var context: Context, var productList: ArrayList<ProductModel>) :
    RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    inner class ViewHolder(var binding: RvItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var binding = RvItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.productImage.load(productList[position].imageUrl){
            placeholder(R.drawable.image_loader)
        }

        holder.binding.productName.text = productList[position].name
        holder.binding.productPrice.text = "₹ " + productList[position].price.toString()
        holder.binding.productDesc.text = productList[position].disp

        holder.itemView.setOnClickListener {
            val productId = productList[position].product_id

            val intent = Intent(holder.itemView.context, DetailActivity::class.java)
            intent.putExtra("PRODUCT_ID", productId)
            holder.itemView.context.startActivity(intent)

        }
    }
}
