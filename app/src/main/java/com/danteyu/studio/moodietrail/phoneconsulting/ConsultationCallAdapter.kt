package com.danteyu.studio.moodietrail.phoneconsulting

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.danteyu.studio.moodietrail.data.ConsultationCall
import com.danteyu.studio.moodietrail.databinding.ItemConsultationCallBinding

/**
 * Created by George Yu on 2020/3/7.
 */
class ConsultationCallAdapter(private val onClickListener: OnClickListener) :
    ListAdapter<ConsultationCall, ConsultationCallAdapter.ConsultationCallViewHolder>(
        DiffCallback
    ) {

    /**
     * Custom listener that handles clicks on [RecyclerView] items.  Passes the [ConsultationCall]
     * associated with the current item to the [onClick] function.
     * @param clickListener lambda that will be called with the current [ConsultationCall]
     */
    class OnClickListener(val clickListener: (consultationCall: ConsultationCall) -> Unit) {
        fun onClick(consultationCall: ConsultationCall) = clickListener(consultationCall)
    }

    class ConsultationCallViewHolder(private var binding: ItemConsultationCallBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(consultationCall: ConsultationCall) {

            binding.consultationCall = consultationCall
            // This is important, because it forces the data binding to execute immediately,
            // which allows the RecyclerView to make the correct view size measurements
            binding.executePendingBindings()
        }
    }

    /**
     * Allows the RecyclerView to determine which items have changed when the [List] of [ConsultationCall]
     * has been updated.
     */
    companion object DiffCallback : DiffUtil.ItemCallback<ConsultationCall>() {
        override fun areItemsTheSame(
            oldItem: ConsultationCall,
            newItem: ConsultationCall
        ): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(
            oldItem: ConsultationCall,
            newItem: ConsultationCall
        ): Boolean {
            return oldItem.id == newItem.id
        }
    }

    /**
     * Create new [RecyclerView] item views (invoked by the layout manager)
     */
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ConsultationCallViewHolder {
        return ConsultationCallViewHolder(
            ItemConsultationCallBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    /**
     * Replaces the contents of a view (invoked by the layout manager)
     */
    override fun onBindViewHolder(holder: ConsultationCallViewHolder, position: Int) {
        val consultationCall = getItem(position)
        holder.itemView.setOnClickListener {
            onClickListener.onClick(consultationCall)
        }
        holder.bind(consultationCall)

    }

}