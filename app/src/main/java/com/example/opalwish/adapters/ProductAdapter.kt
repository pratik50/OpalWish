package com.example.opalwish.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.opalwish.data.ProductModel
import com.example.opalwish.R
import com.example.opalwish.databinding.RvItemBinding
import com.example.opalwish.ui.activity.DetailActivity

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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.productImage.load(productList.get(position).imageUrl){
            placeholder(R.drawable.image_loader)
        }
        holder.binding.productName.text = productList[position].name
        holder.binding.productPrice.text = productList[position].price.toString()
        holder.binding.productCode.text = productList[position].productCode

        holder.itemView.setOnClickListener {

            context.startActivity(
                Intent(context, DetailActivity::class.java).putExtra(
                    "PRODUCT_ID",
                    productList[position].id
                )
            )
        }
    }
}
