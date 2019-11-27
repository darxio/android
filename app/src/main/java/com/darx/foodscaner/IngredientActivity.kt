package com.darx.foodscaner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_ingredient.*
import android.view.MenuItem
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.darx.foodscaner.database.*


class IngredientActivity : AppCompatActivity() {

    private lateinit var ingredientToShow: IngredientModel
    private var isAllowed: Boolean = true
    private var isGroupsMatched: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ingredient)

//        setSupportActionBar(ingredientToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);

        ingredientToShow = intent.extras.get("INGREDIENT") as IngredientModel


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
        groupViewModel.checkAll_(ingredientToShow.groups)?.observe(this@IngredientActivity, object : Observer<Boolean> {
            override fun onChanged(t: Boolean?) {
                isGroupsMatched = t ?: false
                setSettingsByStatus(checkStatus(null))
            }
        })

        // check excepted ingredients
        val ingredientViewModel = ViewModelProviders.of(this).get(IngredientViewModel::class.java)
        ingredientViewModel.getOne_(ingredientToShow.id)?.observe(this@IngredientActivity, object : Observer<IngredientModel> {
            override fun onChanged(t: IngredientModel?) {
                setSettingsByStatus(checkStatus(t))
            }
        })

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

    fun setSettingsByStatus(status: Boolean) {
        if (status) {
            ingredient_exclude_btn.text = resources.getString(R.string.exclude_ingredient)
            ingredient_exclude_btn.setBackgroundColor(resources.getColor(R.color.strongNegativeColor))
        } else {
            ingredient_exclude_btn.text = resources.getString(R.string.add_ingredient)
            ingredient_exclude_btn.setBackgroundColor(resources.getColor(R.color.strongPositiveColor))
        }
    }

    fun checkStatus(ingredient: IngredientModel?): Boolean {
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
