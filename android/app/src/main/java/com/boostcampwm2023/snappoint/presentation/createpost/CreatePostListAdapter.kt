package com.boostcampwm2023.snappoint.presentation.createpost

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.boostcampwm2023.snappoint.databinding.ItemTextBlockBinding

class CreatePostListAdapter(
    private val listener: (Int, String) -> Unit
) : RecyclerView.Adapter<BlockItemViewHolder>() {

    private var blocks: MutableList<PostBlock> = mutableListOf()

    fun getCurrentBlocks() = blocks.toList()

    fun updateBlocks(newBlocks: List<PostBlock>) {
        blocks = newBlocks.toMutableList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlockItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return BlockItemViewHolder(
            ItemTextBlockBinding.inflate(inflater, parent, false),
            listener
        )
    }

    override fun getItemCount(): Int {
        return blocks.size
    }

    override fun onBindViewHolder(holder: BlockItemViewHolder, position: Int) {
        Log.d("TAG", "onBindViewHolder: $position")
        holder.bind(blocks[position].content, position)
    }

    override fun onViewAttachedToWindow(holder: BlockItemViewHolder) {
        super.onViewAttachedToWindow(holder)
        Log.d("TAG", "onViewAttachedToWindow: ")
    }

    override fun onViewDetachedFromWindow(holder: BlockItemViewHolder) {
        super.onViewDetachedFromWindow(holder)
        Log.d("TAG", "onViewDetachedFromWindow: ")
    }
}

@BindingAdapter("blocks", "listener")
fun RecyclerView.bindRecyclerViewAdapter(blocks: List<PostBlock>, listener: (Int, String) -> Unit) {
    if (adapter == null) adapter = CreatePostListAdapter(listener)

    when {
        (adapter as CreatePostListAdapter).getCurrentBlocks().size < blocks.size -> {
            with(adapter as CreatePostListAdapter) {
                updateBlocks(blocks)
                notifyItemInserted(blocks.size - 1)
            }
        }
//
//        (adapter as CreatePostListAdapter).getCurrentBlocks().size == blocks.size -> {
//            with(adapter as CreatePostListAdapter) {
//                updateBlocks(blocks)
//                notifyItemRangeChanged(0, blocks.size)
//            }
//        }
    }
}