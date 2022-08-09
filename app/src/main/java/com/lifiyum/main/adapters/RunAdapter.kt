package com.lifiyum.main.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lifiyum.main.basicactivitys.Countdown_Activity
import com.lifiyum.main.databinding.ItemRunBinding
import com.lifiyum.main.db.Run


class RunAdapter(private val onClickListener: OnClickListener) : ListAdapter<Run, RunAdapter.RunViewHolder>(RunDiffCallback()) {
    private lateinit var  context: Context;
    private lateinit var i:  Intent


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = RunViewHolder.from(parent)

    override fun onBindViewHolder(holder: RunViewHolder, position: Int) {
        context = holder.itemView.getContext()
        var run_obj=Run()
        run_obj=getItem(position)

        holder.itemView.setOnClickListener {
            onClickListener.onClick(run_obj.id)
        }
        holder.bind(getItem(position))

    }

    class RunViewHolder private constructor(
        val binding: ItemRunBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        
        fun bind(item: Run) {
            binding.run = item
            binding.executePendingBindings()

        }

        companion object {
            fun from(parent: ViewGroup): RunViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemRunBinding.inflate(layoutInflater, parent, false)
                return RunViewHolder(binding) } } }

    class RunDiffCallback : DiffUtil.ItemCallback<Run>() {

        override fun areItemsTheSame(oldItem: Run, newItem: Run) = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Run, newItem: Run) =
            oldItem.hashCode() == newItem.hashCode()
    }

    class OnClickListener(val clickListener: (pos: Int?) -> Unit) {
        fun onClick(pos: Int?) = clickListener(pos)
    }
}
