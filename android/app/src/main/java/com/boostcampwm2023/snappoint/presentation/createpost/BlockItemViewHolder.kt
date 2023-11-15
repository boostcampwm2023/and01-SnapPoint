package com.boostcampwm2023.snappoint.presentation.createpost

import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.boostcampwm2023.snappoint.databinding.ItemTextBlockBinding

class BlockItemViewHolder(
    private val binding: ItemTextBlockBinding,
    private val listener: (Int, String) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    private var position = 0

    fun bind(s: String, position: Int) {
        binding.tilText.editText?.setText(s)
        this.position = position
        binding.tilText.editText?.addTextChangedListener {
            listener(position, it.toString())
        }
    }
}