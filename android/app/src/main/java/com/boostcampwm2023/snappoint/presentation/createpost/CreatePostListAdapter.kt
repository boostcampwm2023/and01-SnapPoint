package com.boostcampwm2023.snappoint.presentation.createpost

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.boostcampwm2023.snappoint.databinding.ItemImageBlockBinding
import com.boostcampwm2023.snappoint.databinding.ItemTextBlockBinding

class CreatePostListAdapter(
    private val listener: (Int, String) -> Unit,
    private val onDeleteButtonClicked: (Int) -> Unit,
    private val onEditButtonClicked: (Int) -> Unit,
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

    override fun getItemViewType(position: Int): Int {
        return when(blocks[position]) {
            is PostBlockState.STRING -> ViewType.STRING.ordinal
            is PostBlockState.IMAGE -> ViewType.IMAGE.ordinal
            is PostBlockState.VIDEO -> ViewType.VIDEO.ordinal
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlockItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        when (viewType) {
            ViewType.IMAGE.ordinal -> {
                return BlockItemViewHolder.ImageBlockViewHolder(
                    ItemImageBlockBinding.inflate(inflater, parent, false),
                    listener,
                    onEditButtonClicked
                ) { index ->
                    onDeleteButtonClicked(index)
                    deleteBlocks(index)
                }
            }

            ViewType.VIDEO.ordinal -> {
                TODO()
            }
        }
        return BlockItemViewHolder.TextBlockViewHolder(
            ItemTextBlockBinding.inflate(inflater, parent, false),
            listener,
            onEditButtonClicked
        ) { index ->
            onDeleteButtonClicked(index)
            deleteBlocks(index)
        }
    }

    override fun getItemCount(): Int {
        return blocks.size
    }

    override fun onBindViewHolder(holder: BlockItemViewHolder, position: Int) {
        when (holder) {
            is BlockItemViewHolder.TextBlockViewHolder -> holder.bind(blocks[position] as PostBlockState.STRING, position)
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

    companion object{
        enum class ViewType {
            STRING,
            IMAGE,
            VIDEO,
        }
    }
}

@BindingAdapter("blocks", "listener", "onDeleteButtonClick", "onEditButtonClick")
fun RecyclerView.bindRecyclerViewAdapter(blocks: List<PostBlockState>, listener: (Int, String) -> Unit, onDeleteButtonClicked: (Int) -> Unit, onEditButtonClicked: (Int) -> Unit) {
    if (adapter == null) adapter = CreatePostListAdapter(listener, onDeleteButtonClicked, onEditButtonClicked)

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
                        return@forEachIndexed
                    }
                }
            }
        }
    }
}