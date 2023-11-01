package com.adush.pexelsapp.ui.adapter.images

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.adush.pexelsapp.R
import com.adush.pexelsapp.databinding.ViewItemImageBinding
import com.adush.pexelsapp.domain.model.ImageItem
import com.adush.pexelsapp.ui.utils.ShimmerEffect
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey

class ImageListAdapter(private val onImageItemClick: (Int) -> Unit) :
    PagingDataAdapter<ImageItem, ImageListAdapter.ImageListViewHolder>(
        ImageItemDiffCallback
    ) {

    inner class ImageListViewHolder(private val binding: ViewItemImageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(imageItem: ImageItem) {

            val requestOptions = RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .signature(ObjectKey(System.currentTimeMillis() / (24 * 60 * 60 * 1000)))

            Glide.with(itemView)
                .load(imageItem.imageSrc.large2x)
                .apply(requestOptions)
                .placeholder(ShimmerEffect.getShimmerDrawable())
                .error(R.drawable.ic_placeholder)
                .dontTransform()
                .into(binding.imageView)

            val animation = AnimationUtils.loadAnimation(itemView.context, R.anim.anim_item).apply {
                interpolator = DecelerateInterpolator()
            }
            binding.root.setOnClickListener {
                it.startAnimation(animation)
                onImageItemClick(imageItem.id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageListViewHolder {
        val binding = ViewItemImageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ImageListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageListViewHolder, position: Int) {
        getItem(position)?.let { holder.onBind(it) }
    }
}