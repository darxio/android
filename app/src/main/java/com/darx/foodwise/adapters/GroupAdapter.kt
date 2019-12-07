package com.darx.foodwise.adapters

import android.content.Context
import android.content.res.Resources
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import com.darx.foodwise.R
import com.darx.foodwise.database.GroupModel
import kotlinx.android.synthetic.main.group_item.view.*
import android.widget.LinearLayout
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.darx.foodwise.database.GroupViewModel
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.request.ImageRequestBuilder
import android.net.Uri
import com.facebook.drawee.view.SimpleDraweeView


class GroupAdapter(var items: List<GroupModel>, val groupViewModel: GroupViewModel, val owner: LifecycleOwner, val callback: Callback, var imgSize: Int, var textSize: Int, var ctx: Context) : RecyclerView.Adapter<GroupAdapter.ViewHolder>() {

    val Int.pxToDp: Int
        get() = (this / Resources.getSystem().displayMetrics.density).toInt()

    val Int.pxToDpFloat: Float
        get() = (this / Resources.getSystem().displayMetrics.density)

    val Int.dpToPx: Int
        get() = (this * Resources.getSystem().displayMetrics.density).toInt()

    val Int.dpToPxFloat: Float
        get() = (this * Resources.getSystem().displayMetrics.density)

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

        private val groupImage = itemView.findViewById<SimpleDraweeView>(R.id.group_image)
        private val groupName = itemView.findViewById<TextView>(R.id.group_name)

        fun bind(item: GroupModel) {
//            if (!item.imagePath.isEmpty() || item.imagePath == "NULL") {
//                Picasso.get().load(item.imagePath).error(R.drawable.ic_no_photo).into(groupImage);
//            } else {
//                groupImage.setImageResource(R.drawable.ic_no_photo)
//            }

            if (!item.imagePath.isEmpty() || item.imagePath == "NULL") {
                val request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(item.imagePath))
                    .setProgressiveRenderingEnabled(true)
                    .build()
                val controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(request)
                    .setOldController(groupImage.getController())
                    .build()
                groupImage.setController(controller)
            } else {
                groupImage.setImageResource(com.darx.foodwise.R.drawable.ic_no_photo)
            }

            groupName.text = item.name

            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) callback.onItemClicked(items[adapterPosition])
            }

            if (imgSize > 0) {
                val params = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                params.width = imgSize.dpToPx
                itemView.layoutParams = params
            }

            groupName.textSize = textSize.dpToPxFloat

            groupViewModel.getOne_(item.id)?.observe(owner,
                Observer<GroupModel?> { t ->
                    if (t != null) {
                        itemView.group_icon.visibility = VISIBLE
                    } else {
                        itemView.group_icon.visibility = INVISIBLE
                    }
                })
        }
    }

    interface Callback {
        fun onItemClicked(item: GroupModel)
    }

}