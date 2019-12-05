package com.darx.foodwise.adapters

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.darx.foodwise.R
import com.darx.foodwise.database.GroupViewModel
import com.darx.foodwise.database.IngredientModel
import com.darx.foodwise.database.IngredientViewModel
import com.google.android.material.card.MaterialCardView


class IngredientAdapter(var items: List<IngredientModel>, val ctx: Context, val owner: LifecycleOwner, val ingredientViewModel: IngredientViewModel?, val groupViewModel: GroupViewModel?, val callback: Callback) : RecyclerView.Adapter<IngredientAdapter.ViewHolder>() {

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
        private val ingredientName = itemView.findViewById<TextView>(R.id.ingredient_name)
        private val ingredientImage = itemView.findViewById<ImageView>(R.id.ingredient_eligibility_image)
        private val ingredientInfoIcon = itemView.findViewById<ImageView>(R.id.ingredient_information_ib)
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

            val ingredientName = itemView.findViewById<TextView>(R.id.ingredient_name)
            ingredientName.text = item.name

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

            if (item.description != null && item.description != "NULL") {
                ingredientInfoIcon.visibility = VISIBLE
                ingredientInfoIcon.setOnClickListener {
                    if (adapterPosition != RecyclerView.NO_POSITION) callback.onItemClicked(items[adapterPosition])
                }
            }
        }

        private fun setSettingsByStatus(status: Boolean) {
            if (status) {
                ingredientName.setTextColor(ctx.getColor(R.color.black))
                ingredientObject.setCardBackgroundColor(ctx.getColor(R.color.positiveColor))
                ingredientImage.setImageDrawable(ctx.getDrawable(R.drawable.ic_checkmark_black))
            } else {
                ingredientName.setTextColor(ctx.getColor(R.color.white))
                ingredientObject.setCardBackgroundColor(ctx.getColor(R.color.negativeColor))
                ingredientImage.setImageDrawable(ctx.getDrawable(R.drawable.ic_stop_white))
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