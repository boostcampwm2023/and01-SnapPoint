package com.boostcampwm2023.snappoint.presentation.createpost

import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.boostcampwm2023.snappoint.databinding.ItemImageBlockBinding
import com.boostcampwm2023.snappoint.databinding.ItemTextBlockBinding

sealed class BlockItemViewHolder(
    binding: ViewDataBinding
) : RecyclerView.ViewHolder(binding.root) {

    class TextBlockViewHolder(
        private val binding: ItemTextBlockBinding,
        listener: (Int, String) -> Unit
    ) : BlockItemViewHolder(binding) {

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

    class ImageBlockViewHolder(
        private val binding: ItemImageBlockBinding,
        listener: (Int, String) -> Unit
    ) : BlockItemViewHolder(binding) {

        private val textWatcher = EditTextWatcher(listener)

        fun bind(uri: Uri, position: Int) {
            binding.uri = uri
            textWatcher.updatePosition(position)
        }

        fun attachTextWatcherToEditText() {
            binding.tilDescription.editText?.addTextChangedListener(textWatcher)
        }

        fun detachTextWatcherFromEditText() {
            binding.tilDescription.editText?.removeTextChangedListener(textWatcher)
        }
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


@BindingAdapter("uri")
fun ImageView.bindUri(uri: Uri) {
    setImageURI(uri)
}