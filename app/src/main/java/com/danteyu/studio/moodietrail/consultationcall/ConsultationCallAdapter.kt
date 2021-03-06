package com.danteyu.studio.moodietrail.consultationcall

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.danteyu.studio.moodietrail.MoodieTrailApplication
import com.danteyu.studio.moodietrail.data.ConsultationCall
import com.danteyu.studio.moodietrail.databinding.ItemConsultationCallBinding

/**
 * Created by George Yu on 2020/3/7.
 */
class ConsultationCallAdapter(val viewModel: ConsultationCallViewModel) :
    ListAdapter<ConsultationCall, ConsultationCallAdapter.ConsultationCallViewHolder>(
        DiffCallback
    ) {

    class ConsultationCallViewHolder(
        private var binding: ItemConsultationCallBinding,
        private val viewModel: ConsultationCallViewModel
    ) :
        RecyclerView.ViewHolder(binding.root), LifecycleObserver, LifecycleOwner {

        fun bind(consultationCall: ConsultationCall) {

            binding.lifecycleOwner = this
            binding.viewModel = viewModel
            binding.consultationCall = consultationCall
            consultationCall.serviceHour = consultationCall.serviceHour.replace("\\n", "\n")
            consultationCall.clientele = consultationCall.clientele.replace("\\n", "\n")

            // This is important, because it forces the data binding to execute immediately,
            // which allows the RecyclerView to make the correct view size measurements
            binding.executePendingBindings()
        }

        private val lifecycleRegistry = LifecycleRegistry(this)

        init {
            lifecycleRegistry.currentState = Lifecycle.State.INITIALIZED
        }

        fun onAttach() {
            lifecycleRegistry.currentState = Lifecycle.State.STARTED
        }

        fun onDetach() {
            lifecycleRegistry.currentState = Lifecycle.State.CREATED
        }

        override fun getLifecycle(): Lifecycle {
            return lifecycleRegistry
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
            , viewModel
        )
    }

    /**
     * Replaces the contents of a view (invoked by the layout manager)
     */
    override fun onBindViewHolder(holder: ConsultationCallViewHolder, position: Int) {
        val consultationCall = getItem(position)
        holder.itemView.setOnClickListener {
            dialPhoneNumber(consultationCall.phoneNumber)
        }

        holder.bind(consultationCall)

    }

    override fun onViewAttachedToWindow(holder: ConsultationCallViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.onAttach()
    }

    override fun onViewDetachedFromWindow(holder: ConsultationCallViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.onDetach()
    }

    private fun dialPhoneNumber(phoneNumber: String) {
        val intent = Intent()
        intent.action = Intent.ACTION_DIAL
        intent.data = Uri.parse("tel:${phoneNumber}")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        MoodieTrailApplication.instance.startActivity(intent)
    }

}