package com.darx.foodwise.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.text.BoringLayout
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.darx.foodwise.R
import com.darx.foodwise.database.GroupViewModel
import com.darx.foodwise.database.IngredientModel
import com.darx.foodwise.database.IngredientViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.color.MaterialColors.getColor


class ChipsAdapter(var items: List<IngredientModel>, val ctx: Context, val owner: LifecycleOwner, val ingredientViewModel: IngredientViewModel?, val groupViewModel: GroupViewModel?, val callback: IngredientAdapter.Callback) : RecyclerView.Adapter<ChipsAdapter.ViewHolder>() {

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
        private var isAllowed: Boolean = true
        private var isGroupsMatched: Boolean = false

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
                if (!isAllowed) {
                    if (isGroupsMatched) {
                        item.allowed = true
                        ingredientViewModel?.add_(item)
                    } else {
                        ingredientViewModel?.deleteOne_(item)
                    }
                } else {
                    if (isGroupsMatched) {
                        ingredientViewModel?.deleteOne_(item)
                    } else {
                        item.allowed = false
                        ingredientViewModel?.add_(item)
                    }
                }
            }
        }

        private fun setSettingsByStatus(status: Boolean) {
            if (status) {
                ingredientObject.setTextColor(ctx.getColor(R.color.black))
                ingredientObject.setChipBackgroundColorResource(R.drawable.bg_chip_state_list_positive)
            } else {
                ingredientObject.setTextColor(ctx.getColor(R.color.white))
                ingredientObject.setChipBackgroundColorResource(R.drawable.bg_chip_state_list_negative)
            }
        }

        private fun checkStatus(ingredient: IngredientModel?): Boolean {
            isAllowed = true
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
