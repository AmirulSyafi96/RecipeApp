package com.demo.recipeapp.ui.recipedetails

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

import com.demo.recipeapp.data.Ingredient
import com.demo.recipeapp.data.Step

import com.demo.recipeapp.databinding.ItemIngredientBinding


class StepAdapter(private val listener: OnItemClickListener) :
    ListAdapter<Step, StepAdapter.StepViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StepViewHolder {
        val binding = ItemIngredientBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StepViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StepViewHolder, position: Int) {
        val currentItem = getItem(position)

        holder.bind(currentItem)
    }

    inner class StepViewHolder(private val binding: ItemIngredientBinding) :

        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                root.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val step = getItem(position)
                        listener.onItemClick(step)
                    }
                }
            }
        }

        fun bind(step: Step) {

            binding.apply {
                textViewIngredient.text = step.step
            }

        }
    }

    interface OnItemClickListener {
        fun onItemClick(step: Step)
    }

    class DiffCallback : DiffUtil.ItemCallback<Step>() {
        override fun areItemsTheSame(oldItem: Step, newItem: Step) =
            oldItem.stepId == newItem.stepId

        override fun areContentsTheSame(oldItem: Step, newItem: Step) =
            oldItem == newItem
    }
}