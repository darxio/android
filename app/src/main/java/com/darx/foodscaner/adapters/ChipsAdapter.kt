package com.darx.foodscaner.adapters

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.darx.foodscaner.R
import com.darx.foodscaner.database.GroupViewModel
import com.darx.foodscaner.database.IngredientModel
import com.darx.foodscaner.database.IngredientViewModel
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import com.google.android.material.color.MaterialColors.getColor


class ChipsAdapter(var items: List<IngredientModel>, val owner: LifecycleOwner, val ingredientViewModel: IngredientViewModel?, val groupViewModel: GroupViewModel?, val callback: IngredientAdapter.Callback) : RecyclerView.Adapter<ChipsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.ingredient_chip, parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun addItems(ingredients: List<IngredientModel>) {
        this.items = ingredients
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val ingredientObject = itemView.findViewById<Chip>(R.id.ingredient_chip)
        private var isGroupsMatched = false

        fun bind(item: IngredientModel) {
            // check groups
            if (item.groups == null) item.groups = ArrayList()
            groupViewModel?.checkAll_(item.groups)?.observe(owner,
                Observer<Boolean> { t ->
                    isGroupsMatched = t ?: false
                    setSettingsByStatus(checkStatus(null))
                })

            // check excepted ingredients
            ingredientViewModel?.getOne_(item.id)?.observe(owner,
                Observer<IngredientModel> { t -> setSettingsByStatus(checkStatus(t)) })

            ingredientObject.text = item.name

            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) callback.onItemClicked(items[adapterPosition])
            }
        }

        private fun setSettingsByStatus(status: Boolean) {
            if (status) {
                ingredientObject.setChipBackgroundColorResource(R.drawable.bg_chip_state_list_positive)
            } else {
                ingredientObject.setChipBackgroundColorResource(R.drawable.bg_chip_state_list_negative)
            }
        }

        private fun checkStatus(ingredient: IngredientModel?): Boolean {
            var isAllowed = true
            if (isGroupsMatched) {
                isAllowed = (ingredient != null && ingredient.allowed!!)
            } else {
                if (ingredient != null) {
                    isAllowed = ingredient.allowed!!
                }
            }
            return isAllowed
        }
    }

    interface Callback {
        fun onItemClicked(item: IngredientModel)
    }

}
