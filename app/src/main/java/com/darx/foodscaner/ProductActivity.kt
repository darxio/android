package com.darx.foodscaner

import android.content.Intent
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import com.darx.foodscaner.models.Ingredient
import kotlinx.android.synthetic.main.fragment_product_info.*
import androidx.core.graphics.drawable.toDrawable
import androidx.lifecycle.ViewModelProviders
import com.darx.foodscaner.database.*
import com.google.android.material.chip.Chip


class ProductActivity : AppCompatActivity() {
    private lateinit var productToShow: ProductModel
    private lateinit var pVM: ProductViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_product_info)

        this.productToShow = intent?.extras?.get("PRODUCT") as ProductModel
        this.pVM = ViewModelProviders.of(this).get(ProductViewModel::class.java)

        info_product_name.text = productToShow.name
        info_product_manufacturer.text = productToShow.manufacturer

        var starred = findViewById<ImageButton>(R.id.info_starred_ib)
        var share = findViewById<ImageButton>(R.id.info_share_btn)
        var back = findViewById<Button>(R.id.back_btn)

        // logics with image buttons
        if (productToShow.starred) {
            starred.setBackgroundResource(R.drawable.ic_starred)
        } else {
            starred.setBackgroundResource(R.drawable.ic_unstarred)
        }

        starred.setOnClickListener {
            val starred_ = productToShow.starred
            if (starred_) {
                starred.setBackgroundResource(R.drawable.ic_unstarred)
            } else {
                starred.setBackgroundResource(R.drawable.ic_starred)
            }

            productToShow.starred = !starred_

            pVM.updateStarred_(productToShow)
        }

        share.setOnClickListener {
            val sharingIntent = Intent(Intent.ACTION_SEND)
            sharingIntent.type = "text/plain"
            val shareBody = productToShow.name;
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
            applicationContext.startActivity(
                Intent.createChooser(
                    sharingIntent,
                    //                        ctx.getResources().getString(R.string.share_via)
                    "Поделиться"
                )
            )
        }

        back.setOnClickListener {
            finish()
        }

        val ingredients = this.productToShow.ingredients!!


        for (json in ingredients) {
            val chip: Chip = Chip(this)
            chip.text = json.get()?.getString("name")

            val states = arrayOf(
                intArrayOf(android.R.attr.state_enabled), // enabled
                intArrayOf(-android.R.attr.state_enabled), // disabled
                intArrayOf(-android.R.attr.state_checked), // unchecked
                intArrayOf(android.R.attr.state_pressed)  // pressed
            )
            val colors = intArrayOf(R.color.positiveColor, R.color.positiveColor, R.color.positiveColor, R.color.positiveColor)

            chip.chipBackgroundColor = ColorStateList(states, colors)
            chip.chipStrokeColor = ColorStateList(states, colors)
            chip.chipStrokeWidth = 1F
            chip.chipIcon = R.drawable.ingredient.toDrawable()

            chip.setOnClickListener {
                val intent = Intent(this, IngredientActivity::class.java)
//                intent.putExtra("PRODUCT", item as Serializable)
                startActivity(intent)
            }
            info_ingredient_chips.addView(chip)
        }
    }
}
