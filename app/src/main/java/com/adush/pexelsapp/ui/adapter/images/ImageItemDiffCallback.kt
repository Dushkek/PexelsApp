package com.adush.pexelsapp.ui.adapter.images

import androidx.recyclerview.widget.DiffUtil
import com.adush.pexelsapp.domain.model.ImageItem

object ImageItemDiffCallback: DiffUtil.ItemCallback<ImageItem>() {
    override fun areItemsTheSame(oldItem: ImageItem, newItem: ImageItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ImageItem, newItem: ImageItem): Boolean {
        return oldItem == newItem
    }
}