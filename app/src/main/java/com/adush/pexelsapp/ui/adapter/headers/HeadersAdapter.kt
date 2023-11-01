package com.adush.pexelsapp.ui.adapter.headers

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.adush.pexelsapp.R
import com.adush.pexelsapp.databinding.ViewItemHeaderBinding
import com.adush.pexelsapp.domain.model.FeatureCollection

class HeadersAdapter(private val onHeaderClick: (String) -> Unit) :
    ListAdapter<FeatureCollection, HeadersAdapter.HeadersViewHolder>(HeaderDiffCallback) {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var context: Context
    private var lastSelectedPosition: Int? = null
    private var lastSelectedItem: FeatureCollection? = null
    private var lastSelectedItemView: ViewItemHeaderBinding? = null
    private var mViewHolders: ArrayList<HeadersViewHolder> = arrayListOf()

    inner class HeadersViewHolder(val binding: ViewItemHeaderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(header: FeatureCollection, selectedPosition: Int) {

            binding.headerName.text = header.title

            binding.root.setOnClickListener {
                setInactiveItem()

                setActiveItem(header, selectedPosition, binding)
                onHeaderClick(header.title)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeadersViewHolder {
        val binding = ViewItemHeaderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HeadersViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HeadersViewHolder, position: Int) {
        mViewHolders.add(position, holder)
        holder.onBind(getItem(position), position)
    }

    fun setHeader(header: String) {
        val item = currentList.find {
            it.title.lowercase() == header
        }
        if (header.isEmpty() || item == null) {
            setInactiveItem()
            lastSelectedItem = null
            lastSelectedItemView = null
            lastSelectedPosition = null
            scrollToStart()
        }

        if (item != null && item != lastSelectedItem) {
            setInactiveItem()

            val currentPosition = currentList.indexOf(item)
            val itemBinding = (mViewHolders[currentPosition]).binding
            setActiveItem(item, currentPosition, itemBinding)
        }

    }

    private fun setInactiveItem() {
        lastSelectedItem?.let { it.active = false }
        lastSelectedItemView?.let { lastSelectedItemView ->
            lastSelectedItemView.headerName.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryBlack))
            lastSelectedItemView.root.backgroundTintList = ContextCompat.getColorStateList(context, R.color.colorPrimaryLightGrey)
        }
        lastSelectedPosition?.let { swapItem(0, it) }
    }

    private fun setActiveItem(
        header: FeatureCollection,
        selectedPosition: Int,
        binding: ViewItemHeaderBinding
    ) {
        header.active = true
        lastSelectedPosition = selectedPosition
        lastSelectedItem = currentList[selectedPosition]
        lastSelectedItemView = binding
        swapItem(fromPosition = selectedPosition, toPosition = 0)
        scrollToStart()
        binding.headerName.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryWhite))
        binding.root.backgroundTintList = ContextCompat.getColorStateList(context, R.color.colorPrimaryRed)
    }

    private fun swapItem(fromPosition: Int, toPosition: Int) {
        notifyItemMoved(fromPosition, toPosition)
    }

    private fun scrollToStart() {
        mRecyclerView.scrollToPosition(0)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mRecyclerView = recyclerView
        context = recyclerView.context
    }
}