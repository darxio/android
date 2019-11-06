package com.darx.foodscaner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.darx.foodscaner.database.GroupModel
import com.darx.foodscaner.database.GroupViewModel
import com.darx.foodscaner.database.IngredientModel
import kotlinx.android.synthetic.main.activity_group.*
import kotlinx.android.synthetic.main.activity_ingredient.*

class GroupActivity : AppCompatActivity() {

    private var groupViewModel: GroupViewModel? = null
    private lateinit var groupToShow: GroupModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group)

        setSupportActionBar(groupToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);

        groupViewModel = ViewModelProviders.of(this).get(GroupViewModel::class.java)

        groupToShow = intent.extras.get("GROUP") as GroupModel
        groupCollapsingToolbar.title = groupToShow.name
        infoGroup.text = groupToShow.about
        // collapsingToolbar.background = R.drawable.group.toDrawable() IMAGE

        groupViewModel!!.getOne_(groupToShow.id)?.observe(this@GroupActivity, object : Observer<GroupModel> {
            override fun onChanged(t: GroupModel?) {
                if (t?.id == groupToShow.id) {
                    enterButton.text = "Добавить"
//                    enterButton.setBackgroundColor(R.color.strongPositiveColor)
                }
            }
        })

        enterButton.setOnClickListener {
            if (enterButton.text == "Добавить") {
                groupViewModel!!.deleteOne_(groupToShow)
                enterButton.text = R.string.enter_to_group.toString()
//                enterButton.setBackgroundColor(R.color.strongNegativeColor)
            } else {
                groupViewModel!!.add_(groupToShow)
                enterButton.text = "Добавить"
//                enterButton.setBackgroundColor(R.color.strongPositiveColor)
            }
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
