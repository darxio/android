package com.darx.foodwise.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.darx.foodwise.R
import com.darx.foodwise.database.GroupViewModel
import com.darx.foodwise.database.IngredientModel
import com.darx.foodwise.database.IngredientViewModel
import com.google.android.material.card.MaterialCardView


class IngredientAdapter(var items: List<IngredientModel>, val owner: LifecycleOwner, val ingredientViewModel: IngredientViewModel?, val groupViewModel: GroupViewModel?, val callback: Callback) : RecyclerView.Adapter<IngredientAdapter.ViewHolder>() {

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

        private val ingredientObject = itemView.findViewById<MaterialCardView>(R.id.ingredient_object)
        private var isGroupsMatched = false

        fun bind(item: IngredientModel) {
            // check groups
            if (item.groups == null) item.groups = ArrayList()
            groupViewModel?.checkAll_(item.groups)?.observe(owner, object : Observer<Boolean> {
                override fun onChanged(t: Boolean?) {
                    isGroupsMatched = t ?: false
                    setSettingsByStatus(checkStatus(null))
                }
            })

            // check excepted ingredients
            ingredientViewModel?.getOne_(item.id)?.observe(owner, object : Observer<IngredientModel> {
                override fun onChanged(t: IngredientModel?) {
                    setSettingsByStatus(checkStatus(t))
                }
            })

            val ingredientName = itemView.findViewById<TextView>(R.id.ingredient_name)
            ingredientName.text = item.name

            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) callback.onItemClicked(items[adapterPosition])
            }
        }

        fun setSettingsByStatus(status: Boolean) {
            if (status) {
                ingredientObject.setCardBackgroundColor(R.drawable.bg_chip_state_list_positive)
            } else {
                ingredientObject.setCardBackgroundColor(R.drawable.bg_chip_state_list_negative)
            }
        }

        fun checkStatus(ingredient: IngredientModel?): Boolean {
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