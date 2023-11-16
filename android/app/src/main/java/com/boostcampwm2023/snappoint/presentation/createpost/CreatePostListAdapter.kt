package com.boostcampwm2023.snappoint.presentation.createpost

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.boostcampwm2023.snappoint.databinding.ItemImageBlockBinding
import com.boostcampwm2023.snappoint.databinding.ItemTextBlockBinding

class CreatePostListAdapter(
    private val listener: (Int, String) -> Unit
) : RecyclerView.Adapter<BlockItemViewHolder>() {

    private var blocks: MutableList<PostBlock> = mutableListOf()

    fun getCurrentBlocks() = blocks.toList()

    fun updateBlocks(newBlocks: List<PostBlock>) {
        blocks = newBlocks.toMutableList()
    }

    override fun getItemViewType(position: Int): Int {
        return when(blocks[position]) {
            is PostBlock.STRING -> ViewType.STRING.code
            is PostBlock.IMAGE -> ViewType.IMAGE.code
            is PostBlock.VIDEO -> ViewType.VIDEO.code
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlockItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        when (viewType) {
            ViewType.IMAGE.code -> {
                return BlockItemViewHolder.ImageBlockViewHolder(
                    ItemImageBlockBinding.inflate(inflater, parent, false),
                    listener
                )
            }

            ViewType.VIDEO.code -> {
                TODO()
            }
        }
        return BlockItemViewHolder.TextBlockViewHolder(
            ItemTextBlockBinding.inflate(inflater, parent, false),
            listener
        )
    }

    override fun getItemCount(): Int {
        return blocks.size
    }

    override fun onBindViewHolder(holder: BlockItemViewHolder, position: Int) {
        when (holder) {
            is BlockItemViewHolder.TextBlockViewHolder -> holder.bind(blocks[position].content, position)
            is BlockItemViewHolder.ImageBlockViewHolder -> holder.bind((blocks[position] as PostBlock.IMAGE).uri, position)
        }
    }

    override fun onViewAttachedToWindow(holder: BlockItemViewHolder) {
        super.onViewAttachedToWindow(holder)
        when (holder) {
            is BlockItemViewHolder.TextBlockViewHolder -> holder.attachTextWatcherToEditText()
            is BlockItemViewHolder.ImageBlockViewHolder -> {}
        }
    }

    override fun onViewDetachedFromWindow(holder: BlockItemViewHolder) {
        super.onViewDetachedFromWindow(holder)
        when (holder) {
            is BlockItemViewHolder.TextBlockViewHolder -> holder.detachTextWatcherFromEditText()
            is BlockItemViewHolder.ImageBlockViewHolder -> {}
        }
    }
}

@BindingAdapter("blocks", "listener")
fun RecyclerView.bindRecyclerViewAdapter(blocks: List<PostBlock>, listener: (Int, String) -> Unit) {
    if (adapter == null) adapter = CreatePostListAdapter(listener)

    when {
        // 아이템 추가
        (adapter as CreatePostListAdapter).getCurrentBlocks().size < blocks.size -> {
            with(adapter as CreatePostListAdapter) {
                updateBlocks(blocks)
                notifyItemInserted(blocks.size - 1)
            }
        }

        // content 변경
        (adapter as CreatePostListAdapter).getCurrentBlocks().size == blocks.size -> {
            with(adapter as CreatePostListAdapter) {
                updateBlocks(blocks)
            }
        }
    }
}