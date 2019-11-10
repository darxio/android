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
    private lateinit var ingredientToShow: IngredientModel
    private var isExcept: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ingredient)

        setSupportActionBar(ingredientToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);

        ingredientViewModel = ViewModelProviders.of(this).get(IngredientViewModel::class.java)

        ingredientToShow = intent.extras.get("INGREDIENT") as IngredientModel
        collapsingToolbar.title = ingredientToShow.name
        val desc_html = if (ingredientToShow.description != "NULL") ingredientToShow.description else
            """<p>""" + resources.getString(R.string.no_ingredient_description) +"""</p>""".trimIndent()
        webView.loadDataWithBaseURL("", desc_html, "text/html; charset=utf-8", "utf-8", "")
        // infoIngredient.text = ingredientToShow.description

        // collapsingToolbar.background = R.drawable.ingredient.toDrawable() IMAGE

        ingredientViewModel?.getOne_(ingredientToShow.id)?.observe(this@IngredientActivity, object : Observer<IngredientModel> {
            override fun onChanged(t: IngredientModel?) {
                if (t?.id == ingredientToShow.id) {
                    exceptingButton.text = resources.getString(R.string.add_ingredient)
                    exceptingButton.setBackgroundColor(resources.getColor(R.color.strongPositiveColor))
                    isExcept = true
                } else {
                    exceptingButton.text = resources.getString(R.string.except_ingredient)
                    exceptingButton.setBackgroundColor(resources.getColor(R.color.strongNegativeColor))
                    isExcept = false
                }
            }
        })

        exceptingButton.setOnClickListener {
            if (isExcept) {
                ingredientViewModel?.deleteOne_(ingredientToShow)
            } else {
                ingredientViewModel?.add_(ingredientToShow)
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
