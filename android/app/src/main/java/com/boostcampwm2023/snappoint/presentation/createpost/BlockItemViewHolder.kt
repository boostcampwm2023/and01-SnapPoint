package com.boostcampwm2023.snappoint.presentation.createpost

import androidx.recyclerview.widget.RecyclerView
import com.boostcampwm2023.snappoint.databinding.ItemTextBlockBinding

class BlockItemViewHolder(
    binding: ItemTextBlockBinding,
    listener: CreatePostListAdapter.EditTextChangeListener
) : RecyclerView.ViewHolder(binding.root) {

    private val editText = binding.tilText.editText
    val editTextChangeListener = listener

    init {
        editText?.addTextChangedListener(listener)
    }
}