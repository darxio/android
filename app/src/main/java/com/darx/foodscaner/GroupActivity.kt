package com.darx.foodscaner

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.darx.foodscaner.database.GroupModel
import com.darx.foodscaner.database.GroupViewModel
import com.darx.foodscaner.database.IngredientViewModel
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

        setSupportActionBar(group_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);

        ingredientViewModel = ViewModelProviders.of(this).get(IngredientViewModel::class.java)
        groupViewModel = ViewModelProviders.of(this).get(GroupViewModel::class.java)

        groupToShow = intent.extras.get("GROUP") as GroupModel
        group_name.title = groupToShow.name
        group_info.text = groupToShow.about
        // collapsingToolbar.background = R.drawable.group.toDrawable() IMAGE

        // observer for deleting ingredients of groups
        groupViewModel?.getAllIds()?.observe(this@GroupActivity, object : Observer<List<Int>> {
            override fun onChanged(t: List<Int>?) {
                if (t != null) ingredientViewModel?.deleteIngrOfGroup_(t)
            }
        })

        groupViewModel?.getOne_(groupToShow.id)?.observe(this@GroupActivity, object : Observer<GroupModel> {
            override fun onChanged(t: GroupModel?) {
                if (t?.id == groupToShow.id) {
                    group_join_btn.text = resources.getString(R.string.exit_from_group)
                    group_join_btn.setBackgroundColor(resources.getColor(R.color.strongNegativeColor))
                    isEnter = true
                } else {
                    group_join_btn.text = resources.getString(R.string.enter_to_group)
                    group_join_btn.setBackgroundColor(resources.getColor(R.color.strongPositiveColor))
                    isEnter = false
                }
            }
        })

        group_join_btn.setOnClickListener {
            if (isEnter) {
                groupViewModel?.deleteOne_(groupToShow)
            } else {
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
}
