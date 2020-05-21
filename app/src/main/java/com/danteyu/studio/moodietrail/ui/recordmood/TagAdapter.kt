package com.danteyu.studio.moodietrail.ui.recordmood

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.danteyu.studio.moodietrail.databinding.ItemTagBinding
import com.danteyu.studio.moodietrail.ext.setTouchDelegate

/**
 * Created by George Yu on 2020/2/7.
 * This class implements a [RecyclerView] [ListAdapter] which uses Data Binding to present [List]
 * [String], including computing diffs between lists.
 */
class TagAdapter(private val viewModel: RecordDetailViewModel) :
    ListAdapter<String, TagAdapter.TagViewHolder>(DiffCallback) {

    class TagViewHolder(
        private var binding: ItemTagBinding,
        private val viewModel: RecordDetailViewModel
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(tag: String) {
            binding.tag = tag
            binding.viewModel = viewModel
            binding.imageDeleteTag.setTouchDelegate()
            // This is important, because it forces the data binding to execute immediately,
            // which allows the RecyclerView to make the correct view size measurements
            binding.executePendingBindings()
        }
    }

    /**
     * Allows the RecyclerView to determine which items have changed when the [List] of [String]
     * has been updated.
     */
    companion object DiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    /**
     * Create new [RecyclerView] item views (invoked by the layout manager)
     */
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TagViewHolder {
        return TagViewHolder(
            ItemTagBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), viewModel
        )
    }

    /**
     * Replaces the contents of a view (invoked by the layout manager)
     */
    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        val tag = getItem(position)
        holder.bind(tag)
    }
}