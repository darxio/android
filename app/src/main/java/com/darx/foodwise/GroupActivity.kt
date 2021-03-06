package com.darx.foodwise

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.darx.foodwise.database.GroupModel
import com.darx.foodwise.database.GroupViewModel
import com.darx.foodwise.database.IngredientViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_group.*
import com.darx.foodwise.R as R
import android.graphics.drawable.Drawable
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.view.Menu
import androidx.core.graphics.drawable.toDrawable
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.request.ImageRequestBuilder


class GroupActivity : AppCompatActivity() {

    private var checkIcon: MenuItem? = null
    private var groupViewModel: GroupViewModel? = null
    private var ingredientViewModel: IngredientViewModel? = null
    private lateinit var groupToShow: GroupModel
    private var isEnter: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group)

        setSupportActionBar(group_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);

        ingredientViewModel = ViewModelProviders.of(this).get(IngredientViewModel::class.java)
        groupViewModel = ViewModelProviders.of(this).get(GroupViewModel::class.java)

        groupToShow = intent.extras?.get("GROUP") as GroupModel

        group_collapsing_toolbar.title = groupToShow.name
        group_info.text = groupToShow.about

//        if (!groupToShow.imagePath.isNullOrEmpty() || groupToShow.imagePath == "NULL") {
//            Picasso.get().load(groupToShow.imagePath).error(R.drawable.ic_no_photo).into(
//                object : com.squareup.picasso.Target {
//                    override fun onBitmapFailed(e: java.lang.Exception?, errorDrawable: Drawable?) {}
//
//                    override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
//                        group_collapsing_image.setBackground(bitmap?.toDrawable(resources));
//                    }
//
//                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
//                })
//        } else {
//            group_collapsing_image.setBackgroundResource(R.drawable.ic_no_photo)
//        }

        if (!groupToShow.imagePath.isEmpty() || groupToShow.imagePath == "NULL") {
            val request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(groupToShow.imagePath))
                .setProgressiveRenderingEnabled(true)
                .build()
            val controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(group_collapsing_image.getController())
                .build()
            group_collapsing_image.setController(controller)
        } else {
            group_collapsing_image.setImageResource(com.darx.foodwise.R.drawable.ic_no_photo)
        }

        // collapsingToolbar.background = R.drawable.group.toDrawable() IMAGE

        // observer for deleting ingredients of groups
        groupViewModel?.getAllIds()?.observe(this@GroupActivity,
            Observer<List<Int>> { t ->
                if (t != null) ingredientViewModel?.deleteIngrOfGroup_(t)
            })

        groupViewModel?.getOne_(groupToShow.id)?.observe(this@GroupActivity, object : Observer<GroupModel> {
            override fun onChanged(t: GroupModel?) {
                if (t?.id == groupToShow.id) {
                    group_join_btn.text = resources.getString(R.string.exit_from_group)
                    group_join_btn.setTextColor(getColor(R.color.white))
                    group_join_btn.setBackgroundColor(resources.getColor(R.color.negativeColor))
                    checkIcon?.isVisible = true
                    isEnter = true
                } else {
                    group_join_btn.text = resources.getString(R.string.enter_to_group)
                    group_join_btn.setTextColor(getColor(R.color.black))
                    group_join_btn.setBackgroundColor(resources.getColor(R.color.positiveColor))
                    checkIcon?.isVisible = false
                    isEnter = false
                }
            }
        })

        group_join_btn.setOnClickListener {
            if (isEnter) {
                groupViewModel?.deleteOne_(groupToShow)
            } else {
                groupToShow.isInBase = true
                groupViewModel?.add_(groupToShow)
            }
        }

        group_ingredients_btn.setOnClickListener {
            val intent = Intent(this@GroupActivity, GroupIngredientsActivity::class.java)
            intent.putExtra("GROUP_ID", groupToShow.id)
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.group_toolbar, menu)
        checkIcon = menu?.findItem(R.id.check_icon)

        checkIcon?.isVisible = isEnter
        return true
    }
}
