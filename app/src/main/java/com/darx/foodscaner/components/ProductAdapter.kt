package com.darx.foodscaner.components

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.darx.foodscaner.R


class ProductAdapter(var items: List<Product>, val callback: Callback) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.product_item, parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val productName = itemView.findViewById<TextView>(R.id.productName)
        private val productInfo = itemView.findViewById<TextView>(R.id.productInfo)

        fun bind(item: Product) {
            productName.text = item.name
            productInfo.text = item.info
            // TODO: обработка добавления иконок
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) callback.onItemClicked(items[adapterPosition])
            }
        }
    }

    interface Callback {
        fun onItemClicked(item: Product)
    }

}