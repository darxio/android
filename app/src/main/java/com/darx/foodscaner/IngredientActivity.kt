package com.darx.foodscaner

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.graphics.drawable.toDrawable
import kotlinx.android.synthetic.main.activity_ingredient.*
import android.view.MenuItem
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.darx.foodscaner.database.*


class IngredientActivity : AppCompatActivity() {

    private var ingredientViewModel: IngredientViewModel? = null
    private lateinit var ingredientToShow: IngredientModel


    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ingredient)

        setSupportActionBar(ingredientToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);

        ingredientViewModel = ViewModelProviders.of(this).get(IngredientViewModel::class.java)

        ingredientToShow = intent.extras.get("INGREDIENT") as IngredientModel
        collapsingToolbar.title = ingredientToShow.name
        infoIngredient.text = ingredientToShow.description
//        collapsingToolbar.background = R.drawable.ingredient.toDrawable() IMAGE


        ingredientViewModel!!.getOne_(ingredientToShow.id)?.observe(this@IngredientActivity, object : Observer<IngredientModel> {
            override fun onChanged(t: IngredientModel?) {
                if (t?.id == ingredientToShow.id) {
                    exceptingButton.text = "Добавить"
                    exceptingButton.setBackgroundColor(R.color.strongPositiveColor)
                }
            }
        })

        exceptingButton.setOnClickListener {
            if (exceptingButton.text == "Добавить") {
                ingredientViewModel!!.deleteOne_(ingredientToShow)
                exceptingButton.text = "Исключить"
                exceptingButton.setBackgroundColor(R.color.strongNegativeColor)
            } else {
                ingredientViewModel!!.add_(ingredientToShow)
                exceptingButton.text = "Добавить"
                exceptingButton.setBackgroundColor(R.color.strongPositiveColor)
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
