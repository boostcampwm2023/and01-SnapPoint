package com.boostcampwm2023.snappoint.presentation.main.clusterpreview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.boostcampwm2023.snappoint.databinding.ItemClusterImageBinding
import com.boostcampwm2023.snappoint.presentation.model.PostBlockState

class ClusterListAdapter(private val onItemClicked: (Int) -> Unit) : ListAdapter<PostBlockState, ClusterItemViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClusterItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ClusterItemViewHolder(
            binding = ItemClusterImageBinding.inflate(inflater, parent, false),
            onItemClicked = onItemClicked
        )
    }

    override fun onBindViewHolder(holder: ClusterItemViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<PostBlockState>() {
            override fun areItemsTheSame(oldItem: PostBlockState, newItem: PostBlockState): Boolean {
                return oldItem.uuid == newItem.uuid
            }

            override fun areContentsTheSame(oldItem: PostBlockState, newItem: PostBlockState): Boolean {
                return oldItem == newItem
            }

        }
    }

}

@BindingAdapter("clusters", "onItemClick")
fun RecyclerView.bindRecyclerViewAdapter(blocks: List<PostBlockState>, onItemClicked: (Int) -> Unit) {
    if (adapter == null) adapter = ClusterListAdapter(onItemClicked)
    (adapter as ClusterListAdapter).submitList(blocks)
}