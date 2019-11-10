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
import com.darx.foodscaner.R
import com.darx.foodscaner.database.ProductModel
import com.darx.foodscaner.database.ProductViewModel
import java.text.SimpleDateFormat


class ProductsAdapter(var items: List<ProductModel>, var pVM: ProductViewModel, var ctx: Context, val callback: Callback) : RecyclerView.Adapter<ProductsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.product_item, parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun addItems(products: List<ProductModel>) {
        this.items = products
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val productImage = itemView.findViewById<ImageView>(R.id.product_image)
        private val productName = itemView.findViewById<TextView>(R.id.product_name)
        private val productDescription = itemView.findViewById<TextView>(R.id.product_description)
        private val productWarning = itemView.findViewById<TextView>(R.id.product_warning_text)
        private val productScannedDate = itemView.findViewById<TextView>(R.id.product_date)
        private val starred = itemView.findViewById<ImageButton>(R.id.starred_ib)
        private val share = itemView.findViewById<ImageButton>(R.id.share_btn)
        private val delete = itemView.findViewById<ImageButton>(R.id.delete_ib)

        fun bind(item: ProductModel) {
            productImage.setImageResource(R.drawable.product) //!
            productName.text = item.name
            productDescription.text = item.description

            val dateFormat = SimpleDateFormat("dd MMM, HH:mm")
            productScannedDate.text = dateFormat.format(item.date)

            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) callback.onItemClicked(items[adapterPosition])
            }

            // logics with image buttons
            if (item.starred) {
                starred.setBackgroundResource(R.drawable.ic_starred)
            } else {
                starred.setBackgroundResource(R.drawable.ic_unstarred)
            }

            starred.setOnClickListener {
                val starred_ = item.starred
                if (starred_) {
                    starred.setBackgroundResource(R.drawable.ic_unstarred)
                } else {
                    starred.setBackgroundResource(R.drawable.ic_starred)
                }

                item.starred = !starred_

                pVM.updateStarred_(item)
                notifyDataSetChanged()
            }

            share.setOnClickListener {
                val sharingIntent = Intent(Intent.ACTION_SEND)
                sharingIntent.type = "text/plain"
                val shareBody = item.name;
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
                ctx.startActivity(
                    Intent.createChooser(
                        sharingIntent,
        //                        ctx.getResources().getString(R.string.share_via)
                        "Поделиться"
                    )
                )
            }

            delete.setOnClickListener {
                pVM.deleteOne_(item)
                notifyDataSetChanged()
            }
        }
    }

    interface Callback {
        fun onItemClicked(item: ProductModel)
    }

}