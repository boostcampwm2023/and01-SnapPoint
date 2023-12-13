package com.boostcampwm2023.snappoint.presentation.createpost

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.boostcampwm2023.snappoint.databinding.ItemImageBlockBinding
import com.boostcampwm2023.snappoint.databinding.ItemTextBlockBinding
import com.boostcampwm2023.snappoint.databinding.ItemVideoBlockBinding
import com.boostcampwm2023.snappoint.presentation.model.PostBlockCreationState

class CreatePostListAdapter(
    private val blockItemEvent: BlockItemEventListener
) : RecyclerView.Adapter<BlockItemViewHolder>() {

    private var blocks: MutableList<PostBlockCreationState> = mutableListOf()

    private val itemEvent = object : BlockItemEventListener {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlockItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            PostBlockCreationState.ViewType.TEXT.ordinal -> {
                BlockItemViewHolder.TextBlockViewHolder(
                    binding = ItemTextBlockBinding.inflate(inflater, parent, false),
                    blockItemEvent = itemEvent
                )
            }
            PostBlockCreationState.ViewType.IMAGE.ordinal -> {
                BlockItemViewHolder.ImageBlockViewHolder(
                    binding = ItemImageBlockBinding.inflate(inflater, parent, false),
                    blockItemEvent = itemEvent
                )
            }
            else -> {
                BlockItemViewHolder.VideoBlockViewHolder(
                    binding = ItemVideoBlockBinding.inflate(inflater, parent, false),
                    blockItemEvent = itemEvent
                )
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (blocks[position]) {
            is PostBlockCreationState.TEXT -> PostBlockCreationState.ViewType.TEXT.ordinal
            is PostBlockCreationState.IMAGE -> PostBlockCreationState.ViewType.IMAGE.ordinal
            is PostBlockCreationState.VIDEO -> PostBlockCreationState.ViewType.VIDEO.ordinal
        }
    }

    override fun onBindViewHolder(holder: BlockItemViewHolder, position: Int) {
        when (holder) {
            is BlockItemViewHolder.TextBlockViewHolder -> holder.bind(blocks[position] as PostBlockCreationState.TEXT, position)
            is BlockItemViewHolder.ImageBlockViewHolder -> holder.bind(blocks[position] as PostBlockCreationState.IMAGE, position)
            is BlockItemViewHolder.VideoBlockViewHolder -> holder.bind(blocks[position] as PostBlockCreationState.VIDEO, position)
        }
    }

    fun getCurrentBlocks() = blocks.toList()

    fun updateBlocks(newBlocks: List<PostBlockCreationState>) {
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

    override fun getItemCount(): Int {
        return blocks.size
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
    blocks: List<PostBlockCreationState>,
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
                        is PostBlockCreationState.IMAGE -> {
                            if (postBlock.address != (blocks[index] as PostBlockCreationState.IMAGE).address) {
                                notifyItemChanged(index)
                            }
                        }

                        is PostBlockCreationState.VIDEO -> {
                            if (postBlock.address != (blocks[index] as PostBlockCreationState.VIDEO).address) {
                                notifyItemChanged(index)
                            }else if((postBlock.uri != (blocks[index] as PostBlockCreationState.VIDEO).uri)){
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