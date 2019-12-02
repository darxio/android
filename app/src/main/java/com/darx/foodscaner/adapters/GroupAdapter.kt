package com.darx.foodscaner.adapters

import android.content.res.Resources
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.darx.foodscaner.R
import com.darx.foodscaner.database.GroupModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.group_item.view.*
import android.widget.LinearLayout



class GroupAdapter(var items: List<GroupModel>, val callback: Callback, var n: Int) : RecyclerView.Adapter<GroupAdapter.ViewHolder>() {

    val Int.dp: Int
        get() = (this / Resources.getSystem().displayMetrics.density).toInt()

    val Int.px: Int
        get() = (this * Resources.getSystem().displayMetrics.density).toInt()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.group_item, parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun addItems(groups: List<GroupModel>) {
        this.items = groups
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val groupImage = itemView.findViewById<ImageView>(R.id.group_image)
        private val groupName = itemView.findViewById<TextView>(R.id.group_name)

        fun bind(item: GroupModel) {
            if (!item.imagePath.isEmpty() || item.imagePath == "NULL") {
                Picasso.get().load(item.imagePath).error(R.drawable.ic_no_photo).into(groupImage);
            } else {
                groupImage.setImageResource(R.drawable.ic_no_photo)
            }
            groupName.text = item.name

            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) callback.onItemClicked(items[adapterPosition])
            }

            val params = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            params.width = n.dp
            itemView.layoutParams = params
        }
    }

    interface Callback {
        fun onItemClicked(item: GroupModel)
    }

}