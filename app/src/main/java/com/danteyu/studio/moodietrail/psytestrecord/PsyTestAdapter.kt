package com.danteyu.studio.moodietrail.psytestrecord

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.danteyu.studio.moodietrail.data.PsyTest
import com.danteyu.studio.moodietrail.databinding.ItemPsyTestBinding

/**
 * Created by George Yu on 2020/2/16.
 */
class PsyTestAdapter(private val onClickListener: OnClickListener) :
    ListAdapter<PsyTest, PsyTestAdapter.PsyTestViewHolder>(DiffCallback) {

    /**
     * Custom listener that handles clicks on [RecyclerView] items.  Passes the [PsyTest]
     * associated with the current item to the [onClick] function.
     * @param clickListener lambda that will be called with the current [PsyTest]
     */
    class OnClickListener(val clickListener: (psyTest: PsyTest) -> Unit) {
        fun onClick(psyTest: PsyTest) = clickListener(psyTest)
    }

    class PsyTestViewHolder(private var binding: ItemPsyTestBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(psyTest: PsyTest) {

            binding.psyTest = psyTest
            // This is important, because it forces the data binding to execute immediately,
            // which allows the RecyclerView to make the correct view size measurements
            binding.executePendingBindings()
        }
    }

    /**
     * Allows the RecyclerView to determine which items have changed when the [List] of [PsyTest]
     * has been updated.
     */
    companion object DiffCallback : DiffUtil.ItemCallback<PsyTest>() {
        override fun areItemsTheSame(oldItem: PsyTest, newItem: PsyTest): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: PsyTest, newItem: PsyTest): Boolean {
            return oldItem.id == newItem.id
        }
    }

    /**
     * Create new [RecyclerView] item views (invoked by the layout manager)
     */
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PsyTestViewHolder {
        return PsyTestViewHolder(
            ItemPsyTestBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    /**
     * Replaces the contents of a view (invoked by the layout manager)
     */
    override fun onBindViewHolder(holder: PsyTestViewHolder, position: Int) {
        val psyTest = getItem(position)
        holder.itemView.setOnClickListener {
            onClickListener.onClick(psyTest)
        }
        holder.bind(psyTest)
    }
}