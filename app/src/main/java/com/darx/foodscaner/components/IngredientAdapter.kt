package com.darx.foodscaner.components

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.darx.foodscaner.R
import com.darx.foodscaner.models.Ingredient

class IngredientAdapter(var items: List<Ingredient>, val callback: Callback) : RecyclerView.Adapter<IngredientAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
                = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.ingredient_item, parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val ingredientObject = itemView.findViewById<Button>(R.id.ingredientObject)

        fun bind(item: Ingredient) {
//            ingredientObject.setImageResource(R.drawable.product)  // TODO: сделать подгрузку фоток
            ingredientObject.text = item.name
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) callback.onItemClicked(items[adapterPosition])
            }
        }
    }

    interface Callback {
        fun onItemClicked(item: Ingredient)
    }
}