package com.boostcampwm2023.snappoint.presentation.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.boostcampwm2023.snappoint.databinding.ItemSearchAutoCompleteBinding

class AutoCompletionListAdapter(
    private val onAutoCompleteItemClicked: (Int) -> Unit
) : ListAdapter<String, SearchResultViewHolder>(diffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return SearchResultViewHolder(
            binding = ItemSearchAutoCompleteBinding.inflate(inflater, parent, false),
            onAutoCompleteItemClicked = onAutoCompleteItemClicked
        )
    }

    override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }

        }
    }
}

@BindingAdapter("autoCompleteTexts", "onAutoCompleteItemClick")
fun RecyclerView.bindRecyclerViewAdapter(texts: List<String>, onAutoCompleteItemClicked:(Int) -> Unit) {
    if (adapter == null) adapter = AutoCompletionListAdapter(onAutoCompleteItemClicked)
    (adapter as AutoCompletionListAdapter).submitList(texts)
}