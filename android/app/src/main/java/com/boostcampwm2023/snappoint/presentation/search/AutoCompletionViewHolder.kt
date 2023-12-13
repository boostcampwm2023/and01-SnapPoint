package com.boostcampwm2023.snappoint.presentation.search

import androidx.recyclerview.widget.RecyclerView
import com.boostcampwm2023.snappoint.databinding.ItemSearchAutoCompleteBinding

class AutoCompletionViewHolder(
    private val binding: ItemSearchAutoCompleteBinding,
    private val onAutoCompleteItemClicked: (Int) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(string: String, index: Int) {

        with(binding) {
            item = string
            root.setOnClickListener { onAutoCompleteItemClicked(index) }
        }
    }
}