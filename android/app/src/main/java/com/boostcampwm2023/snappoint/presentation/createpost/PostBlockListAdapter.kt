package com.boostcampwm2023.snappoint.presentation.createpost

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.boostcampwm2023.snappoint.databinding.ItemTextBlockBinding

class PostBlockListAdapter(
    private val listener: (Int, String) -> Unit
) : ListAdapter<PostBlock, BlockItemViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlockItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return BlockItemViewHolder(
            ItemTextBlockBinding.inflate(inflater, parent, false),
            listener
        )
    }

    override fun onBindViewHolder(holder: BlockItemViewHolder, position: Int) {

        holder.bind(getItem(position).content, position)
    }

    companion object {

        val diffUtil = object : DiffUtil.ItemCallback<PostBlock>() {
            override fun areItemsTheSame(oldItem: PostBlock, newItem: PostBlock): Boolean {
//                return when{
//                    oldItem is PostBlock.STRING && newItem is PostBlock.STRING -> true
//                    oldItem is PostBlock.IMAGE && newItem is PostBlock.IMAGE -> true
//                    oldItem is PostBlock.VIDEO && newItem is PostBlock.VIDEO -> true
//                    else -> false
//                }
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: PostBlock, newItem: PostBlock): Boolean {
                return oldItem.content == newItem.content
            }
        }
    }

}

//@BindingAdapter("blocks", "listener")
//fun bindAdapter(recyclerView: RecyclerView, blocks: List<PostBlock>, listener: (position: Int, content: String) ->Unit){
//    if(recyclerView.adapter == null) recyclerView.adapter = PostBlockListAdapter{ position, value ->
//        Log.d("TAG", "bindAdapter: $position $value")
//        listener.invoke(position, value)
//    }
//    if(blocks.size != (recyclerView.adapter as PostBlockListAdapter).itemCount){
//        Log.d("TAG", "bindAdapter: ${blocks.joinToString(" ")}")
//        (recyclerView.adapter as PostBlockListAdapter).submitList(blocks)
//    }
//}