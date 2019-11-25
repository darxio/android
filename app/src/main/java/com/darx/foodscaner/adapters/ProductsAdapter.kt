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


class ProductsAdapter(var items: List<ProductModel>, var pVM: ProductViewModel, val ctx: Context, val owner: LifecycleOwner, val scanedElements: Boolean, val callback: Callback) : RecyclerView.Adapter<ProductsAdapter.ViewHolder>() {

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

        private val productImage = itemView.findViewById<ImageView>(R.id.pr_image)
        private val productName = itemView.findViewById<TextView>(R.id.pr_name)
        private val productDescription = itemView.findViewById<TextView>(R.id.pr_description)
        private val productScannedDate = itemView.findViewById<TextView>(R.id.pr_date)
        private val starred = itemView.findViewById<ImageButton>(R.id.pr_starred_ib)
        private val share = itemView.findViewById<ImageButton>(R.id.pr_share_ib)
        private val delete = itemView.findViewById<ImageButton>(R.id.pr_delete_ib)

        fun bind(item: ProductModel) {
            productName.text = item.name
            productDescription.text = item.description

            if (!item.image.isNullOrEmpty() || item.image == "NULL") {
                Picasso.get().load(item.image).error(R.drawable.product).into(productImage);
            } else {
                productImage.setImageResource(R.drawable.product)
            }

            if (scanedElements) {
                val dateFormat = SimpleDateFormat("dd MMM, HH:mm")
                productScannedDate.text = dateFormat.format(item.date)
            } else {
                productScannedDate.visibility = View.INVISIBLE
                delete.visibility = View.INVISIBLE
            }

            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) callback.onItemClicked(items[adapterPosition])
            }

            pVM.getOne_(item.barcode)?.observe(owner, object : Observer<ProductModel> {
                override fun onChanged(t: ProductModel?) {
                    item.starred = t?.starred ?: item.starred
                    if (t != null && t.starred) {
                        starred.setBackgroundResource(R.drawable.ic_starred)
                    } else {
                        starred.setBackgroundResource(R.drawable.ic_unstarred)
                    }
                }
            })

            // logics with image buttons
            starred.setOnClickListener {
                item.starred = !item.starred
                if (item.starred) {
                    pVM.upsert_(item)
                } else {
                    if (item.scanned) {
                        pVM.upsert_(item)
                    } else {
                        pVM.deleteOne_(item)
                    }
                }
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
                if (item.starred) {
                    item.scanned = !item.scanned
                    pVM.upsert_(item)
                } else {
                    pVM.deleteOne_(item)
                }
            }
        }
    }

    interface Callback {
        fun onItemClicked(item: ProductModel)
    }

}