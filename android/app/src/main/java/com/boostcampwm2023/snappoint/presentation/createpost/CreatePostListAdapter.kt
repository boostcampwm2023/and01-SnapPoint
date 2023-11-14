package com.boostcampwm2023.snappoint.presentation.createpost

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.boostcampwm2023.snappoint.databinding.ItemTextBlockBinding

class CreatePostListAdapter(private val uiState: CreatePostUiState) :
    RecyclerView.Adapter<BlockItemViewHolder>() {

    var blocks: MutableList<PostBlock> = uiState.postBlocks

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlockItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return BlockItemViewHolder(
            ItemTextBlockBinding.inflate(inflater, parent, false),
            EditTextChangeListener(uiState = uiState)
        )
    }

    override fun getItemCount(): Int {
        return blocks.size
    }

    override fun onBindViewHolder(holder: BlockItemViewHolder, position: Int) {
        holder.listener.updatePosition(position)
    }
}

class EditTextChangeListener(private val uiState: CreatePostUiState) : TextWatcher {

    private var position: Int = 0

    fun updatePosition(new: Int) {
        position = new
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        uiState.onTextChanged(position, s.toString())
    }

    override fun afterTextChanged(s: Editable?) {

    }
}

@BindingAdapter("textWatcher")
fun EditText.addTextChangeListener(listener: TextWatcher) {
    addTextChangedListener(listener)
}