package com.demo.recipeapp.ui.recipes

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.demo.recipeapp.R
import com.demo.recipeapp.data.Recipe
import com.demo.recipeapp.databinding.ItemRecipeBinding

class RecipesAdapter(private val listener: OnItemClickListener) :
    ListAdapter<Recipe, RecipesAdapter.RecipesViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipesViewHolder {
        val binding = ItemRecipeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecipesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipesViewHolder, position: Int) {
        val currentItem = getItem(position)

        holder.bind(currentItem)
    }

    inner class RecipesViewHolder(private val binding: ItemRecipeBinding) :

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
                /*checkBoxCompleted.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val task = getItem(position)
                        listener.onCheckBoxClick(task, checkBoxCompleted.isChecked)
                    }
                }*/
            }
        }

        fun bind(recipe: Recipe) {

            binding.apply {

                textViewName.text = recipe.name
                textViewDescription.text = recipe.description
                //val padded = String.format(Locale.UK,"%02d", (holder.adapterPosition+1))
                //textViewCount.text = padded
              /*  if (recipe.file.isNotEmpty()) {

                }else{imageViewRecipe.setBackgroundResource(R.drawable.ic_image);}*/

                Glide.with(imageViewRecipe.context)
                    .load(recipe.path)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .placeholder(R.drawable.ic_image)

                    .into(imageViewRecipe)
            }

        }
    }

    interface OnItemClickListener {
        fun onItemClick(recipe: Recipe)
        //fun onCheckBoxClick(task: Task, isChecked: Boolean)
    }

    class DiffCallback : DiffUtil.ItemCallback<Recipe>() {
        override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe) =
            oldItem.recipeId == newItem.recipeId

        override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe) =
            oldItem == newItem
    }
}