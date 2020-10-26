package com.darx.foodwise

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_ingredient.*
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.darx.foodwise.database.*
import com.darx.foodwise.database.GroupViewModel
import com.darx.foodwise.database.IngredientModel
import com.darx.foodwise.database.IngredientViewModel
import kotlinx.android.synthetic.main.activity_group.*
import kotlinx.android.synthetic.main.ingredient_item.*


class IngredientActivity : AppCompatActivity() {

    private lateinit var ingredientToShow: IngredientModel
    private var isAllowed: Boolean = true
    private var isGroupsMatched: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ingredient)

        setSupportActionBar(ingredient_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);

        ingredientToShow = intent.extras?.get("INGREDIENT") as IngredientModel

        ingredient_name.text = ingredientToShow.name

        if (ingredientToShow.danger != 0 && ingredientToShow.danger!! > -1) {
            when (ingredientToShow.danger) {
                3 -> warning_icon.setImageDrawable(getDrawable(R.drawable.ic_warning_first))
                4 -> warning_icon.setImageDrawable(getDrawable(R.drawable.ic_warning_second))
                5 -> warning_icon.setImageDrawable(getDrawable(R.drawable.ic_warning_third))
            }
            warning_icon.visibility = View.VISIBLE
        }

//        collapsingToolbar.title = ingredientToShow.name
        val desc_html = if (ingredientToShow.description != "NULL") ingredientToShow.description else """
            <p>""" + resources.getString(R.string.no_ingredient_description) +"""</p>
        """.trimIndent()
        ingredient_web_view.loadDataWithBaseURL("", desc_html, "text/html", "UTF-8", "")
        // collapsingToolbar.background = R.drawable.ingredient.toDrawable() IMAGE


        if (ingredientToShow.groups.isNullOrEmpty()) {
            ingredientToShow.groups = ArrayList()
        }

        // check groups
        val groupViewModel = ViewModelProviders.of(this).get(GroupViewModel::class.java)
        groupViewModel.checkAll_(ingredientToShow.groups)?.observe(this@IngredientActivity,
            Observer<Boolean> { t ->
                isGroupsMatched = t ?: false
                setSettingsByStatus(checkStatus(null))
            })

        // check excepted ingredients
        val ingredientViewModel = ViewModelProviders.of(this).get(IngredientViewModel::class.java)
        ingredientViewModel.getOne_(ingredientToShow.id)?.observe(this@IngredientActivity,
            Observer<IngredientModel> { t -> setSettingsByStatus(checkStatus(t)) })

        ingredient_exclude_btn.setOnClickListener {
            if (!isAllowed) {
                if (isGroupsMatched) {
                    ingredientToShow.allowed = true
                    ingredientViewModel.add_(ingredientToShow)
                } else {
                    ingredientViewModel.deleteOne_(ingredientToShow)
                }
            } else {
                if (isGroupsMatched) {
                    ingredientViewModel.deleteOne_(ingredientToShow)
                } else {
                    ingredientToShow.allowed = false
                    ingredientViewModel.add_(ingredientToShow)
                }
            }
        }
    }

    private fun setSettingsByStatus(status: Boolean) {
        if (status) {
            ingredient_exclude_btn.text = resources.getString(R.string.exclude_ingredient)
            ingredient_exclude_btn.setTextColor(getColor(R.color.white))
            ingredient_exclude_btn.setBackgroundColor(getColor(R.color.negativeColor))
            ingredient_name.setTextColor(getColor(R.color.black))
            ingredient_object.setCardBackgroundColor(getColor(R.color.positiveColor))
            ingredient_eligibility_image.setImageDrawable(getDrawable(R.drawable.ic_checkmark_black))
        } else {
            ingredient_exclude_btn.text = resources.getString(R.string.add_ingredient)
            ingredient_exclude_btn.setTextColor(getColor(R.color.black))
            ingredient_exclude_btn.setBackgroundColor(getColor(R.color.positiveColor))
            ingredient_name.setTextColor(getColor(R.color.white))
            ingredient_object.setCardBackgroundColor(getColor(R.color.negativeColor))
            ingredient_eligibility_image.setImageDrawable(getDrawable(R.drawable.ic_stop_white))
        }
    }

    private fun checkStatus(ingredient: IngredientModel?): Boolean {
        isAllowed = true
        if (isGroupsMatched) {
            isAllowed = (ingredient != null && ingredient.allowed!!)
        } else {
            if (ingredient != null) {
                isAllowed = ingredient.allowed!!
            }
        }
        return isAllowed
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
