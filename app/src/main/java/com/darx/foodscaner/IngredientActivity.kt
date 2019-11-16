package com.darx.foodscaner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_ingredient.*
import android.view.MenuItem
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.darx.foodscaner.database.*


class IngredientActivity : AppCompatActivity() {

    private var ingredientViewModel: IngredientViewModel? = null
    private var groupViewModel: GroupViewModel? = null
    private lateinit var ingredientToShow: IngredientModel
    private var isExcept: Boolean = false
    private var isGroup: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ingredient)

        setSupportActionBar(ingredientToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);

        ingredientViewModel = ViewModelProviders.of(this).get(IngredientViewModel::class.java)
        groupViewModel = ViewModelProviders.of(this).get(GroupViewModel::class.java)

        ingredientToShow = intent.extras.get("INGREDIENT") as IngredientModel
        collapsingToolbar.title = ingredientToShow.name
        val desc_html = if (ingredientToShow.description != "NULL") ingredientToShow.description else """
            <p>""" + resources.getString(R.string.no_ingredient_description) +"""</p>
        """.trimIndent()

        webView.loadDataWithBaseURL("", desc_html, "text/html", "UTF-8", "")

        // collapsingToolbar.background = R.drawable.ingredient.toDrawable() IMAGE

        ingredientViewModel?.getOne_(ingredientToShow.id)?.observe(this@IngredientActivity, object : Observer<IngredientModel> {
            override fun onChanged(t: IngredientModel?) {
                if (t?.allowed == false) {
                    exceptingButton.text = resources.getString(R.string.add_ingredient)
                    exceptingButton.setBackgroundColor(resources.getColor(R.color.strongPositiveColor))
                    isExcept = true
                } else {
                    exceptingButton.text = resources.getString(R.string.except_ingredient)
                    exceptingButton.setBackgroundColor(resources.getColor(R.color.strongNegativeColor))
                    if (t?.allowed == true) {
                        isGroup = true
                    }
                    isExcept = false
                }
            }
        })

        exceptingButton.setOnClickListener {
            if (isExcept) {
//                var isProductInMyGroup = false
//                for (groupId in ingredientToShow?.groups!!) {
//                    val group = groupViewModel?.getOne_(groupId)
//                    if (group?.value as Boolean) {
//                        isProductInMyGroup = true
//                    }
//                }
//
//                if (isProductInMyGroup) {
//                    ingredientToShow.allowed = true
//                    ingredientViewModel?.add_(ingredientToShow)
//                } else {
                    ingredientViewModel?.deleteOne_(ingredientToShow)
//                }
            } else {
//                if (isGroup) {
//                    ingredientViewModel?.deleteOne_(ingredientToShow)
//                } else {
                    ingredientViewModel?.add_(ingredientToShow)
//                }
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
