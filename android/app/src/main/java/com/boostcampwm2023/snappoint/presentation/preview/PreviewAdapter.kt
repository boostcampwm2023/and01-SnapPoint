package com.boostcampwm2023.snappoint.presentation.preview

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.boostcampwm2023.snappoint.databinding.ItemImagePreviewBinding
import com.boostcampwm2023.snappoint.presentation.around.PostListAdapter
import com.boostcampwm2023.snappoint.presentation.model.PostBlockState

class PreviewAdapter : RecyclerView.Adapter<PreviewViewHolder>() {

    private var mediaBlocks: List<PostBlockState> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreviewViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return PreviewViewHolder(ItemImagePreviewBinding.inflate(inflater, parent, false))
    }

    override fun getItemCount(): Int {
        return mediaBlocks.size
    }

    override fun onBindViewHolder(holder: PreviewViewHolder, position: Int) {
        mediaBlocks[position].let { postBlock ->
            when (postBlock) {
                is PostBlockState.IMAGE -> holder.bind(Uri.parse(postBlock.content), postBlock.description)
                is PostBlockState.VIDEO -> holder.bind(Uri.parse(postBlock.content), postBlock.description)
                is PostBlockState.STRING -> {}
            }
        }
    }

    fun updateList(blocks: List<PostBlockState>) {
        mediaBlocks = blocks.filter { (it is PostBlockState.STRING).not() }
        notifyDataSetChanged()
    }
}

@BindingAdapter("blocks")
fun RecyclerView.bindRecyclerViewAdapter(blocks: List<PostBlockState>){
    if (adapter == null) adapter = PreviewAdapter()
    (adapter as PreviewAdapter).updateList(blocks)
}