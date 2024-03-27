package com.example.opalwish.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.opalwish.R

class OfferSliderAdapter(private val imageList:ArrayList<Int>,private val viewPager2: ViewPager2):
    RecyclerView.Adapter<OfferSliderAdapter.ImageViewHolder>()
{
    inner class ImageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val offer_image_slider: ImageView = itemView.findViewById(R.id.offer_image_slider)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.offer_slider_item,parent,false))
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.offer_image_slider.setImageResource(imageList[position])
        if (position == imageList.size - 1){
            viewPager2.post(runnable)
        }
    }

    private val runnable = Runnable {
        imageList.addAll(imageList)
        notifyDataSetChanged()
    }
}