package com.boostcampwm2023.snappoint.presentation.createpost

import androidx.recyclerview.widget.RecyclerView
import com.boostcampwm2023.snappoint.databinding.ItemTextBlockBinding

class BlockItemViewHolder(
    binding: ItemTextBlockBinding,
    val listener: EditTextChangeListener
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.listener = listener
    }
}