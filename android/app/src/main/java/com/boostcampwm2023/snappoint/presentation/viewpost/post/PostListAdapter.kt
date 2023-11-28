package com.boostcampwm2023.snappoint.presentation.viewpost.post

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.boostcampwm2023.snappoint.databinding.ItemImagePostBinding
import com.boostcampwm2023.snappoint.databinding.ItemTextPostBinding
import com.boostcampwm2023.snappoint.presentation.model.PostBlockState

class PostListAdapter : ListAdapter<PostBlockState, RecyclerView.ViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        when (viewType) {
            PostBlockState.ViewType.IMAGE.ordinal -> {
                return ImageBlockViewHolder(ItemImagePostBinding.inflate(inflater, parent, false))
            }
            PostBlockState.ViewType.VIDEO.ordinal -> {
                TODO()
            }
        }
        return TextBlockViewHolder(ItemTextPostBinding.inflate(inflater, parent, false))
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is PostBlockState.TEXT -> PostBlockState.ViewType.STRING.ordinal
            is PostBlockState.IMAGE -> PostBlockState.ViewType.IMAGE.ordinal
            is PostBlockState.VIDEO -> PostBlockState.ViewType.VIDEO.ordinal
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TextBlockViewHolder -> {
                holder.bind(getItem(position) as PostBlockState.TEXT)
            }
            is ImageBlockViewHolder -> {
                holder.bind(getItem(position) as PostBlockState.IMAGE)
            }
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<PostBlockState>() {
            override fun areItemsTheSame(oldItem: PostBlockState, newItem: PostBlockState): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: PostBlockState, newItem: PostBlockState): Boolean {
                return oldItem == newItem
            }

        }
    }
}

@BindingAdapter("postBlocks")
fun RecyclerView.bindRecyclerViewAdapter(postBlocks: List<PostBlockState>) {
    if (adapter == null) adapter = PostListAdapter()
    (adapter as PostListAdapter).submitList(postBlocks)
}