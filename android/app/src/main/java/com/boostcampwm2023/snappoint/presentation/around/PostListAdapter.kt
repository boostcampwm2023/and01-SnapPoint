package com.boostcampwm2023.snappoint.presentation.around

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.boostcampwm2023.snappoint.databinding.ItemAroundPostBinding

class PostListAdapter(private val onExpandButtonClicked: (Int) -> Unit) :
    ListAdapter<PostState, PostItemViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return PostItemViewHolder(
            binding = ItemAroundPostBinding.inflate(inflater, parent, false),
            onExpandButtonClicked = onExpandButtonClicked
        )
    }

    override fun onBindViewHolder(holder: PostItemViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<PostState>() {
            override fun areItemsTheSame(oldItem: PostState, newItem: PostState): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: PostState, newItem: PostState): Boolean {
                return oldItem == newItem
            }

        }
    }
}

@BindingAdapter("posts", "onExpandButtonClick")
fun RecyclerView.bindRecyclerViewAdapter(
    posts: List<PostState>,
    onExpandButtonClicked: (Int) -> Unit
) {
    if (adapter == null) adapter = PostListAdapter(onExpandButtonClicked = onExpandButtonClicked)
    (adapter as PostListAdapter).submitList(posts)
}