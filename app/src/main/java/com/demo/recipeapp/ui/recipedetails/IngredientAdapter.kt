package com.demo.recipeapp.ui.recipedetails

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

import com.demo.recipeapp.data.Ingredient

import com.demo.recipeapp.databinding.ItemIngredientBinding


class IngredientAdapter(private val listener: OnItemClickListener) :
    ListAdapter<Ingredient, IngredientAdapter.IngredientViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val binding = ItemIngredientBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return IngredientViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        val currentItem = getItem(position)

        holder.bind(currentItem)
    }

    inner class IngredientViewHolder(private val binding: ItemIngredientBinding) :

        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                root.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val task = getItem(position)
                        listener.onItemClick(task)
                    }
                }
            }
        }

        fun bind(ingredient: Ingredient) {

            binding.apply {
                textViewIngredient.text = ingredient.ingredient
            }

        }
    }

    interface OnItemClickListener {
        fun onItemClick(ingredient: Ingredient)
    }

    class DiffCallback : DiffUtil.ItemCallback<Ingredient>() {
        override fun areItemsTheSame(oldItem: Ingredient, newItem: Ingredient) =
            oldItem.ingredientId == newItem.ingredientId

        override fun areContentsTheSame(oldItem: Ingredient, newItem: Ingredient) =
            oldItem == newItem
    }
}