package com.darx.foodscaner.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.darx.foodscaner.R
import com.darx.foodscaner.database.ScannedProductModel


class ProductsAdapter(var items: List<ScannedProductModel>, val callback: Callback) : RecyclerView.Adapter<ProductsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.product_item, parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val productImage = itemView.findViewById<ImageView>(R.id.product_image)
        private val productName = itemView.findViewById<TextView>(R.id.product_name)
        private val productDescription = itemView.findViewById<TextView>(R.id.product_description)
        private val productWarning = itemView.findViewById<TextView>(R.id.product_warning_text)


        fun bind(item: ScannedProductModel) {
            productImage.setImageResource(R.drawable.product) //!
            productName.text = item.name
            productDescription.text = item.description
            productDescription.text = item.description
            productWarning.text = "" //!!


            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) callback.onItemClicked(items[adapterPosition])
            }
        }
    }

    interface Callback {
        fun onItemClicked(item: ScannedProductModel)
    }

}