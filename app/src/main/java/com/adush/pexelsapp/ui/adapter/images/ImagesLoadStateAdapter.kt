package com.adush.pexelsapp.ui.adapter.images

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.adush.pexelsapp.R
import com.adush.pexelsapp.databinding.ViewItemLoadStateBinding

class ImagesLoadStateAdapter(private val retry: () -> Unit) :
    LoadStateAdapter<ImagesLoadStateAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ViewItemLoadStateBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.errorMsg.setOnClickListener { retry.invoke() }
        }

        fun onBind(loadState: LoadState) {
            if (loadState is LoadState.Error)
                binding.errorMsg.text = itemView.context.getString(R.string.err_poor_internet_connection)

            binding.progressBar.isVisible = loadState is LoadState.Loading
            binding.errorMsg.isVisible = loadState !is LoadState.Loading
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, loadState: LoadState) {
        holder.onBind(loadState)

    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): ViewHolder {
        val binding = ViewItemLoadStateBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding)
    }
}