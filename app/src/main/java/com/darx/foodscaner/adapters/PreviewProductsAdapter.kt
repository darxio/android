package com.darx.foodscaner.adapters

import android.content.Context
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.darx.foodscaner.database.ProductModel
import com.darx.foodscaner.database.ProductViewModel
import java.text.SimpleDateFormat
import android.graphics.BitmapFactory
import android.R.attr.src
import android.util.Log
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.darx.foodscaner.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_product.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.*
import java.io.IOException
import java.net.HttpURLConnection


class PreviewProductsAdapter(var items: List<ProductModel>, val callback: Callback) : RecyclerView.Adapter<PreviewProductsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.favourite_product_preview_item, parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun addItems(products: List<ProductModel>) {
        this.items = products
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val productImage = itemView.findViewById<ImageView>(R.id.favourite_product_preview_image)

        fun bind(item: ProductModel) {

            if (!item.image.isNullOrEmpty() || item.image == "NULL") {
                Picasso.get().load(item.image).error(R.drawable.ic_no_photo).into(productImage);
            } else {
                productImage.setImageResource(R.drawable.ic_no_photo)
            }

            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) callback.onItemClicked(items[adapterPosition])
            }
        }
    }

    interface Callback {
        fun onItemClicked(item: ProductModel)
    }

}