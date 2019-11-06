package com.darx.foodscaner.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.toDrawable
import com.darx.foodscaner.R
import com.darx.foodscaner.database.GroupModel
import com.darx.foodscaner.database.IngredientModel
import com.darx.foodscaner.models.Group
import com.google.android.material.chip.Chip


class IngredientAdapter(var items: List<IngredientModel>, val callback: Callback) : RecyclerView.Adapter<IngredientAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.ingredient_item, parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun addItems(ingredients: List<IngredientModel>) {
        this.items = ingredients
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val ingredientObject = itemView.findViewById<Chip>(R.id.ingredientObject)

        fun bind(item: IngredientModel) {
            ingredientObject.chipIcon = R.drawable.ingredient.toDrawable()
            ingredientObject.text = item.name

            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) callback.onItemClicked(items[adapterPosition])
            }
        }
    }

    interface Callback {
        fun onItemClicked(item: IngredientModel)
    }

}