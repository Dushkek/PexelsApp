package com.adush.pexelsapp.ui.adapter

import androidx.recyclerview.widget.DiffUtil
import com.adush.pexelsapp.domain.model.FeatureCollection

object HeaderDiffCallback: DiffUtil.ItemCallback<FeatureCollection>() {
    override fun areItemsTheSame(oldItem: FeatureCollection, newItem: FeatureCollection): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: FeatureCollection, newItem: FeatureCollection): Boolean {
        return oldItem == newItem
    }
}