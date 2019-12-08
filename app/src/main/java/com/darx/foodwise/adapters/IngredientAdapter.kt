package com.darx.foodwise.adapters

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
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


class IngredientAdapter(var items: List<IngredientModel>, val ctx: Context, val callback: Callback, val callbackInfo: CallbackInfo) : RecyclerView.Adapter<IngredientAdapter.ViewHolder>() {

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
        private val ingredientWarningIcon = itemView.findViewById<ImageView>(R.id.warning_icon)

        fun bind(item: IngredientModel) {
            val ingredientName = itemView.findViewById<TextView>(R.id.ingredient_name)
            ingredientName.text = item.name

            if (item.danger != 0 && item.danger!! > -1) {
                when (item.danger) {
                    3 -> ingredientWarningIcon.setImageDrawable(ctx.getDrawable(R.drawable.ic_warning_first))
                    4 -> ingredientWarningIcon.setImageDrawable(ctx.getDrawable(R.drawable.ic_warning_second))
                    5 -> ingredientWarningIcon.setImageDrawable(ctx.getDrawable(R.drawable.ic_warning_third))
                }
                ingredientWarningIcon.visibility = VISIBLE
            } else {
                ingredientWarningIcon.visibility = GONE
            }

            itemView.setOnClickListener {
                callback.onItemClicked(items[adapterPosition])
            }

            if (item.description != null && item.description != "NULL" && item.description != "") {
                ingredientInfoIcon.visibility = VISIBLE
                ingredientInfoIcon.setOnClickListener {
                    if (adapterPosition != RecyclerView.NO_POSITION) callbackInfo.onItemClicked(items[adapterPosition])
                }
            } else {
                ingredientInfoIcon.visibility = GONE
            }

            setSettingsByStatus(item.ok)
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
    }

    interface Callback {
        fun onItemClicked(item: IngredientModel)
    }

    interface CallbackInfo {
        fun onItemClicked(item: IngredientModel)
    }

}