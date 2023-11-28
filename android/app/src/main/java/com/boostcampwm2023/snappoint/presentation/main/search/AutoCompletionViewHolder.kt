package com.boostcampwm2023.snappoint.presentation.main.search

import androidx.recyclerview.widget.RecyclerView
import com.boostcampwm2023.snappoint.databinding.ItemSearchAutoCompleteBinding

class AutoCompletionViewHolder(
    private val binding: ItemSearchAutoCompleteBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(string: String) {

        with(binding) {
            item = string
        }
    }
}