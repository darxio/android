package com.darx.foodwise.adapters

import android.content.Context
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import com.darx.foodwise.database.ProductModel
import com.darx.foodwise.database.ProductViewModel
import java.text.SimpleDateFormat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.darx.foodwise.R
import com.darx.foodwise.database.IngredientExtended
import com.darx.foodwise.database.IngredientModel
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.card.MaterialCardView
import com.squareup.picasso.Picasso


class ProductsAdapter(var items: List<ProductModel>, val pVM: ProductViewModel, val ctx: Context, val scanedElements: Boolean, val callback: Callback) : RecyclerView.Adapter<ProductsAdapter.ViewHolder>() {

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
        private val productScannedDate = itemView.findViewById<TextView>(R.id.pr_date)
        private val starred = itemView.findViewById<ImageButton>(R.id.pr_starred_ib)
        private val share = itemView.findViewById<ImageButton>(R.id.pr_share_ib)
        private val delete = itemView.findViewById<ImageButton>(R.id.pr_delete_ib)

        private val productCard = itemView.findViewById<MaterialCardView>(R.id.product_card)

        fun bind(item: ProductModel) {
            productName.text = item.name

            if (!item.image.isNullOrEmpty() || item.image == "NULL") {
                Picasso.get().load(item.image).error(R.drawable.ic_cereals__black)
                    .into(productImage);
            } else {
                productImage.setImageResource(R.drawable.ic_cereals__black)
            }

            if (scanedElements) {
                val dateFormat = SimpleDateFormat("dd MMM, HH:mm")
                productScannedDate.text = dateFormat.format(item.date)
            } else {
                productScannedDate.visibility = View.GONE
                delete.visibility = View.GONE
            }

            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) callback.onItemClicked(items[adapterPosition])
            }

            if (item.starred) {
                starred.setBackgroundResource(R.drawable.ic_star_yellow)
            } else {
                if (item.ok == 0) {
                    starred.setBackgroundResource(R.drawable.ic_star_black)
                } else {
                    starred.setBackgroundResource(R.drawable.ic_star_white)
                }
            }

            setSettingsByStatus(item.ok)

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
//                Snackbar.make(itemView, "Replace with your own action", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show();
            }
        }

        private fun setSettingsByStatus(status: Int) {
            if (status == 0) {
                productName.setTextColor(ctx.getColor(R.color.black))
                productCard.setCardBackgroundColor(ctx.getColor(R.color.positiveColor))
                productScannedDate.setTextColor(ctx.getColor(R.color.black))
//            ingredientImage.setImageDrawable(ctx.getDrawable(R.drawable.ic_checkmark_black))
                share.setImageDrawable(ctx.getDrawable(R.drawable.ic_share_black))
                delete.setImageDrawable(ctx.getDrawable(R.drawable.ic_garbage_black))
            } else {
                productName.setTextColor(ctx.getColor(R.color.white))
                productCard.setCardBackgroundColor(ctx.getColor(R.color.negativeColor))
                productScannedDate.setTextColor(ctx.getColor(R.color.white))
//            ingredientImage.setImageDrawable(ctx.getDrawable(R.drawable.ic_stop_white))
                share.setImageDrawable(ctx.getDrawable(R.drawable.ic_share_white))
                delete.setImageDrawable(ctx.getDrawable(R.drawable.ic_garbage_white))
            }
        }
    }

    interface Callback {
        fun onItemClicked(item: ProductModel)
    }

}