package com.adush.pexelsapp.ui.adapter.bookmarks

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.adush.pexelsapp.R
import com.adush.pexelsapp.databinding.ViewItemBookmarkBinding
import com.adush.pexelsapp.domain.model.ImageItem
import com.adush.pexelsapp.ui.adapter.images.ImageItemDiffCallback
import com.adush.pexelsapp.ui.utils.ShimmerEffect
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions

class BookmarksAdapter(private val onImageItemClick: (Int) -> Unit) :
    PagingDataAdapter<ImageItem, BookmarksAdapter.BookmarksViewHolder>(
        ImageItemDiffCallback
    ) {

    inner class BookmarksViewHolder(private val binding: ViewItemBookmarkBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(imageItem: ImageItem) {

            binding.authorName.text = imageItem.author

            val requestOptions = RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)

            Glide.with(itemView)
                .load(imageItem.imageSrc.large2x)
                .apply(requestOptions)
                .placeholder(ShimmerEffect.getShimmerDrawable())
                .error(R.drawable.ic_placeholder)
                .dontTransform()
                .into(binding.imageView)

            val animation = AnimationUtils.loadAnimation(itemView.context, R.anim.anim_item).apply {
                interpolator = LinearOutSlowInInterpolator()
            }
            binding.root.setOnClickListener {
                it.startAnimation(animation)
                onImageItemClick(imageItem.id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarksViewHolder {
        val binding = ViewItemBookmarkBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BookmarksViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookmarksViewHolder, position: Int) {
        getItem(position)?.let { holder.onBind(it) }
    }
}