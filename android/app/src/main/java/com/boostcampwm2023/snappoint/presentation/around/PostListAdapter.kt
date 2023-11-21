package com.boostcampwm2023.snappoint.presentation.around

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.boostcampwm2023.snappoint.databinding.ItemAroundPostBinding
import com.boostcampwm2023.snappoint.presentation.model.PostSummaryState

class PostListAdapter(
) : ListAdapter<PostSummaryState, PostItemViewHolder>(diffUtil) {

    private var isExpandedList: MutableList<Boolean> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return PostItemViewHolder(
            binding = ItemAroundPostBinding.inflate(inflater, parent, false),
            onExpandButtonClicked = { index ->
                isExpandedList[index] = isExpandedList[index].not()
            }
        )
    }

    override fun onBindViewHolder(holder: PostItemViewHolder, position: Int) {
        holder.bind(getItem(position), position, isExpandedList[position])
    }

    override fun onCurrentListChanged(
        previousList: MutableList<PostSummaryState>, currentList: MutableList<PostSummaryState>
    ) {
        super.onCurrentListChanged(previousList, currentList)
        isExpandedList = currentList.map { false }.toMutableList()
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<PostSummaryState>() {
            override fun areItemsTheSame(oldItem: PostSummaryState, newItem: PostSummaryState): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: PostSummaryState, newItem: PostSummaryState): Boolean {
                return oldItem == newItem
            }

        }
    }
}

@BindingAdapter("posts")
fun RecyclerView.bindRecyclerViewAdapter(posts: List<PostSummaryState>) {
    if (adapter == null) adapter = PostListAdapter()
    (adapter as PostListAdapter).submitList(posts)
}