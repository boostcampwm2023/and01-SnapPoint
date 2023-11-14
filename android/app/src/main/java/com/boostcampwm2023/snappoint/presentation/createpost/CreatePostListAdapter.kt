package com.boostcampwm2023.snappoint.presentation.createpost

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.boostcampwm2023.snappoint.databinding.ItemTextBlockBinding

class CreatePostListAdapter(
    private val viewModel: CreatePostViewModel
) : RecyclerView.Adapter<BlockItemViewHolder>() {

    var blocks = viewModel.uiState.value.postBlocks.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlockItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return BlockItemViewHolder(
            ItemTextBlockBinding.inflate(inflater, parent, false),
            EditTextChangeListener()
        )
    }

    override fun getItemCount(): Int {
        return blocks.size
    }

    override fun onBindViewHolder(holder: BlockItemViewHolder, position: Int) {
        holder.editTextChangeListener.updatePosition(position)
    }

    inner class EditTextChangeListener : TextWatcher {

        private var position = 0

        fun updatePosition(new: Int) {
            position = new
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            viewModel.updatePostBlocks(position, s.toString())

            when (val postBlock = blocks[position]) {
                is PostBlock.STRING -> blocks[position] = postBlock.copy(s.toString())
                is PostBlock.IMAGE -> TODO()
                is PostBlock.VIDEO -> TODO()
            }
        }

        override fun afterTextChanged(s: Editable?) {

        }

    }
}