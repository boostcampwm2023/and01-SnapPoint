package com.boostcampwm2023.snappoint.presentation.createpost

import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.boostcampwm2023.snappoint.databinding.ItemImageBlockBinding
import com.boostcampwm2023.snappoint.databinding.ItemTextBlockBinding
import com.google.android.material.card.MaterialCardView

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
        private val onEditButtonClicked: (Int) -> Unit,
        private val onCheckButtonClicked: (Int) -> Unit,
        private val onDeleteButtonClicked: (Int) -> Unit,
    ) : BlockItemViewHolder(binding, listener) {

        fun bind(block: PostBlockState.STRING, position: Int) {
            binding.tilText.editText?.setText(block.content)
            binding.onDeleteButtonClick = { onDeleteButtonClicked(position) }
            binding.btnEditBlock.setOnClickListener { onEditButtonClicked(position) }
            binding.btnEditComplete.setOnClickListener { onCheckButtonClicked(position) }
            binding.editMode = block.isEditMode
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
        private val onEditButtonClicked: (Int) -> Unit,
        private val onCheckButtonClicked: (Int) -> Unit,
        private val onDeleteButtonClicked: (Int) -> Unit,
    ) : BlockItemViewHolder(binding, listener) {

        fun bind(block: PostBlockState.IMAGE, position: Int) {
            binding.tilDescription.editText?.setText(block.content)
            binding.onDeleteButtonClick = { onDeleteButtonClicked(position) }
            binding.btnEditBlock.setOnClickListener { onEditButtonClicked(position) }
            binding.btnEditComplete.setOnClickListener { onCheckButtonClicked(position) }
            binding.uri = block.uri
            binding.editMode = block.isEditMode
            textWatcher.updatePosition(position)
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

@BindingAdapter("editMode")
fun MaterialCardView.isEditMode(editMode: Boolean) {
    val value = TypedValue()
    val width = if (editMode) {
        context.theme.resolveAttribute(
            com.google.android.material.R.attr.colorSecondary,
            value,
            true
        )
        4
    } else {
        context.theme.resolveAttribute(com.google.android.material.R.attr.colorOutline, value, true)
        1
    }
    strokeWidth = width
    strokeColor = value.data
}