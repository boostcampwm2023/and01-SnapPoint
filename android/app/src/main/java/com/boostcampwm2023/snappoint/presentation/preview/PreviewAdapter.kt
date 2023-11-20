package com.boostcampwm2023.snappoint.presentation.preview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.boostcampwm2023.snappoint.databinding.ItemImagePreviewBinding
import com.boostcampwm2023.snappoint.presentation.createpost.PostBlockState

class PreviewAdapter(private val blocks: List<PostBlockState>) : RecyclerView.Adapter<PreviewViewHolder>() {

    private val mediaBlocks: List<PostBlockState> = blocks.filter { (it is PostBlockState.STRING).not() }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreviewViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return PreviewViewHolder(ItemImagePreviewBinding.inflate(inflater, parent, false))
    }

    override fun getItemCount(): Int {
        return mediaBlocks.size
    }

    override fun onBindViewHolder(holder: PreviewViewHolder, position: Int) {
        with(mediaBlocks[position]) {
            when (this) {
                is PostBlockState.IMAGE -> holder.bind(this.uri, this.content)
                is PostBlockState.VIDEO -> holder.bind(this.uri, this.content)
                is PostBlockState.STRING -> {}
            }
        }
    }
}