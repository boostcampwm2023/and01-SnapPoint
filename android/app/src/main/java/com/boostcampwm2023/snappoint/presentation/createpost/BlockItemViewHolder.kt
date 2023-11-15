package com.boostcampwm2023.snappoint.presentation.createpost

import android.text.Editable
import android.text.TextWatcher
import androidx.recyclerview.widget.RecyclerView
import com.boostcampwm2023.snappoint.databinding.ItemTextBlockBinding

class BlockItemViewHolder(
    private val binding: ItemTextBlockBinding,
    listener: (Int, String) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    private val textWatcher = EditTextWatcher(listener)

    fun bind(text: String, position: Int) {
        binding.tilText.editText?.setText(text)
        textWatcher.updatePosition(position)
    }

    fun attachTextWatcherToEditText() {
        binding.tilText.editText?.addTextChangedListener(textWatcher)
    }

    fun detachTextWatcherFromEditText() {
        binding.tilText.editText?.removeTextChangedListener(textWatcher)
    }
}

class EditTextWatcher(private val listener: (Int, String) -> Unit) : TextWatcher {

    private var position = 0

    fun updatePosition(position: Int) {
        this.position = position
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }

    override fun afterTextChanged(changedText: Editable?) {
        listener(position, changedText.toString())
    }
}