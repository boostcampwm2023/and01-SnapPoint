package com.boostcampwm2023.snappoint.presentation.createpost

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.boostcampwm2023.snappoint.databinding.ItemImageBlockBinding
import com.boostcampwm2023.snappoint.databinding.ItemTextBlockBinding
import com.boostcampwm2023.snappoint.presentation.model.PostBlockState

class CreatePostListAdapter(
    private val blockItemEvent: BlockItemEventListener
) : RecyclerView.Adapter<BlockItemViewHolder>() {

    private var blocks: MutableList<PostBlockState> = mutableListOf()

    fun getCurrentBlocks() = blocks.toList()

    fun updateBlocks(newBlocks: List<PostBlockState>) {
        blocks = newBlocks.toMutableList()
    }

    private fun deleteBlocks(position: Int) {
        blocks.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, blocks.size - position)
    }

    private fun moveUpBlock(position: Int) {
        if (position == 0) return
        val tmp = blocks[position]
        blocks[position] = blocks[position - 1]
        blocks[position - 1] = tmp
        notifyItemRangeChanged(position - 1, 2)
    }

    private fun moveDownBlock(position: Int) {
        if (position == blocks.lastIndex) return
        val tmp = blocks[position]
        blocks[position] = blocks[position + 1]
        blocks[position + 1] = tmp
        notifyItemRangeChanged(position, 2)
    }

    override fun getItemViewType(position: Int): Int {
        return when (blocks[position]) {
            is PostBlockState.TEXT -> PostBlockState.ViewType.STRING.ordinal
            is PostBlockState.IMAGE -> PostBlockState.ViewType.IMAGE.ordinal
            is PostBlockState.VIDEO -> PostBlockState.ViewType.VIDEO.ordinal
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlockItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemEvent = object : BlockItemEventListener {
            override val onTextChange: (Int, String) -> Unit = blockItemEvent.onTextChange
            override val onDeleteButtonClick: (Int) -> Unit = { index ->
                blockItemEvent.onDeleteButtonClick(index)
                deleteBlocks(index)
            }
            override val onEditButtonClick: (Int) -> Unit = blockItemEvent.onEditButtonClick
            override val onCheckButtonClick: (Int) -> Unit = blockItemEvent.onCheckButtonClick
            override val onUpButtonClick: (Int) -> Unit = { index ->
                blockItemEvent.onUpButtonClick(index)
                moveUpBlock(index)
            }
            override val onDownButtonClick: (Int) -> Unit = { index ->
                blockItemEvent.onDownButtonClick(index)
                moveDownBlock(index)
            }
            override val onAddressIconClick: (Int) -> Unit = blockItemEvent.onAddressIconClick
        }

        when (viewType) {
            PostBlockState.ViewType.IMAGE.ordinal -> {
                return BlockItemViewHolder.ImageBlockViewHolder(
                    binding = ItemImageBlockBinding.inflate(inflater, parent, false),
                    blockItemEvent = itemEvent
                )
            }

            PostBlockState.ViewType.VIDEO.ordinal -> {
                TODO()
            }
        }
        return BlockItemViewHolder.TextBlockViewHolder(
            binding = ItemTextBlockBinding.inflate(inflater, parent, false),
            blockItemEvent = itemEvent
        )
    }

    override fun getItemCount(): Int {
        return blocks.size
    }

    override fun onBindViewHolder(holder: BlockItemViewHolder, position: Int) {
        when (holder) {
            is BlockItemViewHolder.TextBlockViewHolder -> holder.bind(blocks[position] as PostBlockState.TEXT, position)
            is BlockItemViewHolder.ImageBlockViewHolder -> holder.bind(blocks[position] as PostBlockState.IMAGE, position)
        }
    }

    override fun onViewAttachedToWindow(holder: BlockItemViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.attachTextWatcherToEditText()
    }

    override fun onViewDetachedFromWindow(holder: BlockItemViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.detachTextWatcherFromEditText()
    }
}

@BindingAdapter("blocks", "blockItemEvent")
fun RecyclerView.bindRecyclerViewAdapter(
    blocks: List<PostBlockState>,
    blockItemEvent: BlockItemEventListener
) {
    if (adapter == null) adapter = CreatePostListAdapter(blockItemEvent)

    when {
        // 아이템 추가
        (adapter as CreatePostListAdapter).getCurrentBlocks().size < blocks.size -> {
            with(adapter as CreatePostListAdapter) {
                updateBlocks(blocks)
                notifyItemInserted(blocks.size - 1)
            }
        }

        // content 또는 editMode 변경
        (adapter as CreatePostListAdapter).getCurrentBlocks().size == blocks.size -> {
            with(adapter as CreatePostListAdapter) {
                val current = (adapter as CreatePostListAdapter).getCurrentBlocks()
                updateBlocks(blocks)
                current.forEachIndexed { index, postBlock ->
                    if (postBlock.isEditMode != blocks[index].isEditMode) {
                        val on = blocks.indexOfFirst { it.isEditMode }
                        val off = current.indexOfFirst { it.isEditMode }
                        notifyItemChanged(on)
                        notifyItemChanged(off)
                    }
                    when (postBlock) {
                        is PostBlockState.IMAGE -> {
                            if (postBlock.address != (blocks[index] as PostBlockState.IMAGE).address) {
                                notifyItemChanged(index)
                            }
                        }

                        is PostBlockState.VIDEO -> {
                            if (postBlock.address != (blocks[index] as PostBlockState.VIDEO).address) {
                                notifyItemChanged(index)
                            }
                        }

                        else -> {}
                    }
                }
            }
        }
    }
}