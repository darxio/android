package com.darx.foodscaner.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.darx.foodscaner.R
import com.darx.foodscaner.database.GroupModel


class GroupAdapter(var items: List<GroupModel>, val callback: Callback) : RecyclerView.Adapter<GroupAdapter.ViewHolder>() {

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

        private val groupImage = itemView.findViewById<ImageView>(R.id.groupImage)
        private val groupName = itemView.findViewById<TextView>(R.id.groupName)

        fun bind(item: GroupModel) {
            groupImage.setImageResource(R.drawable.product)  // TODO: сделать подгрузку фоток
            groupName.text = item.name

            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) callback.onItemClicked(items[adapterPosition])
            }
        }
    }

    interface Callback {
        fun onItemClicked(item: GroupModel)
    }

}