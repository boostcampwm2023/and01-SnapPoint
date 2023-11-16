package com.boostcampwm2023.snappoint.presentation.createpost

import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.boostcampwm2023.snappoint.databinding.ItemImageBlockBinding
import com.boostcampwm2023.snappoint.databinding.ItemTextBlockBinding

sealed class BlockItemViewHolder(
    binding: ViewDataBinding,
    listener: (Int, String) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    val textWatcher = EditTextWatcher(listener)

    abstract fun attachTextWatcherToEditText()
    abstract fun detachTextWatcherFromEditText()

    class TextBlockViewHolder(
        private val binding: ItemTextBlockBinding,
        listener: (Int, String) -> Unit,
        private val onDeleteButtonClicked: (Int) -> Unit
    ) : BlockItemViewHolder(binding, listener) {

        fun bind(content: String, position: Int) {
            binding.tilText.editText?.setText(content)
            binding.onDeleteButtonClick = { onDeleteButtonClicked(position) }
            textWatcher.updatePosition(position)
        }

        override fun attachTextWatcherToEditText() {
            binding.tilText.editText?.addTextChangedListener(textWatcher)
        }

        override fun detachTextWatcherFromEditText() {
            binding.tilText.editText?.removeTextChangedListener(textWatcher)
        }
    }

    class ImageBlockViewHolder(
        private val binding: ItemImageBlockBinding,
        listener: (Int, String) -> Unit,
        private val onAddressIconClicked: (Int) -> Unit,
        private val onDeleteButtonClicked: (Int) -> Unit
    ) : BlockItemViewHolder(binding, listener) {

        fun bind(content: String, uri: Uri, index: Int) {
            binding.tilDescription.editText?.setText(content)
            binding.onDeleteButtonClick = { onDeleteButtonClicked(index) }
            binding.uri = uri
            binding.tilAddress.setEndIconOnClickListener { onAddressIconClicked.invoke(index) }
            textWatcher.updatePosition(index)
        }

        override fun attachTextWatcherToEditText() {
            binding.tilDescription.editText?.addTextChangedListener(textWatcher)
        }

        override fun detachTextWatcherFromEditText() {
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
    load(uri)
}