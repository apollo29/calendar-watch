package com.apollo29.calendarwatch.ui.welcome

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.apollo29.calendarwatch.databinding.ViewWelcomeImageItemBinding

class WelcomeImageAdapter(private val imageList: List<Int>) :
    RecyclerView.Adapter<WelcomeImageAdapter.ViewPagerViewHolder>() {

    inner class ViewPagerViewHolder(val binding: ViewWelcomeImageItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun setData(image: Int) {
            binding.ivImage.setImageResource(image)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerViewHolder {
        val binding = ViewWelcomeImageItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewPagerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewPagerViewHolder, position: Int) {
        holder.setData(imageList[position])
    }

    override fun getItemCount() = imageList.size

}