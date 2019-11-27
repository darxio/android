package com.darx.foodscaner

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.core.graphics.drawable.toDrawable
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.darx.foodscaner.database.GroupModel
import com.darx.foodscaner.database.GroupViewModel
import com.darx.foodscaner.database.IngredientViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_group.*
import com.darx.foodscaner.R as R

class GroupActivity : AppCompatActivity() {

    private var groupViewModel: GroupViewModel? = null
    private var ingredientViewModel: IngredientViewModel? = null
    private lateinit var groupToShow: GroupModel
    private var isEnter: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group)

        setSupportActionBar(groupToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);

        ingredientViewModel = ViewModelProviders.of(this).get(IngredientViewModel::class.java)
        groupViewModel = ViewModelProviders.of(this).get(GroupViewModel::class.java)

        groupToShow = intent.extras.get("GROUP") as GroupModel
        groupCollapsingToolbar.title = groupToShow.name
        infoGroup.text = groupToShow.about
        // collapsingToolbar.background = R.drawable.group.toDrawable() IMAGE

        if (!groupToShow.imagePath.isNullOrEmpty() || groupToShow.imagePath == "NULL") {
            Picasso.get().load(groupToShow.imagePath).error(R.drawable.product).into(
                object : com.squareup.picasso.Target {
                    override fun onBitmapFailed(e: java.lang.Exception?, errorDrawable: Drawable?) {}

                    override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                        groupCollapsingToolbar.setBackground(bitmap?.toDrawable(resources))
                    }

                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
                })
        } else {
            groupCollapsingToolbar.setBackgroundResource(R.drawable.product)
        }

        // observer for deleting ingredients of groups
        groupViewModel?.getAllIds()?.observe(this@GroupActivity, object : Observer<List<Int>> {
            override fun onChanged(t: List<Int>?) {
                if (t != null) ingredientViewModel?.deleteIngrOfGroup_(t)
            }
        })

        groupViewModel?.getOne_(groupToShow.id)?.observe(this@GroupActivity, object : Observer<GroupModel> {
            override fun onChanged(t: GroupModel?) {
                if (t?.id == groupToShow.id) {
                    enterButton.text = resources.getString(R.string.exit_from_group)
                    enterButton.setBackgroundColor(resources.getColor(R.color.strongNegativeColor))
                    isEnter = true
                } else {
                    enterButton.text = resources.getString(R.string.enter_to_group)
                    enterButton.setBackgroundColor(resources.getColor(R.color.strongPositiveColor))
                    isEnter = false
                }
            }
        })

        enterButton.setOnClickListener {
            if (isEnter) {
                groupViewModel?.deleteOne_(groupToShow)
            } else {
                groupViewModel?.add_(groupToShow)
            }
        }

        groupIngredientsButton.setOnClickListener {
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
}
