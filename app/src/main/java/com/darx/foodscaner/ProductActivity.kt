package com.darx.foodscaner

import android.content.Intent
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import kotlinx.android.synthetic.main.fragment_product_info.*
import androidx.core.graphics.drawable.toDrawable
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.darx.foodscaner.database.*
import com.darx.foodscaner.models.IngredientExtended
import com.darx.foodscaner.services.ApiService
import com.darx.foodscaner.services.ConnectivityInterceptorImpl
import com.darx.foodscaner.services.NetworkDataSourceImpl
import com.google.android.material.chip.Chip
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class ProductActivity : AppCompatActivity() {
    private lateinit var productToShow: ProductModel
    private lateinit var pVM: ProductViewModel
    private var networkDataSource: NetworkDataSourceImpl? = null
    var chips: ArrayList<Chip>? = null

    val states = arrayOf(
        intArrayOf(android.R.attr.state_enabled), // enabled
        intArrayOf(-android.R.attr.state_enabled), // disabled
        intArrayOf(-android.R.attr.state_checked), // unchecked
        intArrayOf(android.R.attr.state_pressed)  // pressed
    )

    val positiveColors = intArrayOf(R.color.positiveColor, R.color.positiveColor, R.color.positiveColor, R.color.positiveColor)
    val negativeColors = intArrayOf(R.color.negativeColor, R.color.negativeColor, R.color.negativeColor, R.color.negativeColor)

    private fun preorder(ingredient: IngredientExtended) {
        if (ingredient == null) {
            return
        }

        // logics
        val chip = Chip(this)
        chip.text = ingredient.name
        this.chips?.add(chip)

        chip.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                networkDataSource!!.getIngredientByID(ingredient.id)
            }
        }

        chip.chipBackgroundColor = ColorStateList(states, positiveColors)
        chip.chipStrokeColor = ColorStateList(states, positiveColors)
//        chip.chipStrokeWidth = 1F
        chip.chipIcon = R.drawable.ingredient.toDrawable()


        if (ingredient.ingredients == null || !productToShow.ingredients!!.isEmpty()) {
            return
        }
        for (i in ingredient.ingredients!!) {
            preorder(ingredient);
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_product_info)

        val apiService = ApiService(ConnectivityInterceptorImpl(this))
        networkDataSource = NetworkDataSourceImpl(apiService)
        networkDataSource?.ingredient?.observe(this, Observer {
            val intent = Intent(this, IngredientActivity::class.java)
            intent.putExtra("INGREDIENT", it)
            startActivity(intent)
        })

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

        if ((productToShow.ingredients!![0] != null) || (!productToShow.ingredients!!.isEmpty())) {
            preorder(productToShow.ingredients!![0])
        }

        if (this.chips != null) {
            for (i in this.chips!!) {
               info_ingredient_chips.addView(i)
            }
        }
    }
}
