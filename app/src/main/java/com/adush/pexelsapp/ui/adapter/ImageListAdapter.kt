package com.adush.pexelsapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.adush.pexelsapp.R
import com.adush.pexelsapp.databinding.ViewItemImageBinding
import com.adush.pexelsapp.domain.model.ImageItem
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerDrawable

class ImageListAdapter(private val onImageItemClick: () -> Unit) :
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
                .placeholder(getShimmerDrawable())
                .error(R.drawable.ic_placeholder)
                .dontTransform()
                .into(binding.imageView)

            binding.root.setOnClickListener {
                onImageItemClick()
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

    private fun getShimmerDrawable(): ShimmerDrawable{
        val shimmer = Shimmer.AlphaHighlightBuilder()
            .setDuration(1000)
            .setBaseAlpha(0.8f)
            .setHighlightAlpha(0.6f)
            .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
            .setAutoStart(true)
            .build()

        return ShimmerDrawable().apply {
            setShimmer(shimmer)
        }
    }
}