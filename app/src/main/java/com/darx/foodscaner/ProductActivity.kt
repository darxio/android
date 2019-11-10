package com.darx.foodscaner


import android.content.Intent
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.core.graphics.drawable.toDrawable
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.darx.foodscaner.database.*
import com.darx.foodscaner.models.IngredientExtended
import com.darx.foodscaner.services.ApiService
import com.darx.foodscaner.services.ConnectivityInterceptorImpl
import com.darx.foodscaner.services.NetworkDataSourceImpl
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.activity_product.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import android.widget.TextView


class ProductActivity : AppCompatActivity() {
    private lateinit var productToShow: ProductModel
    private lateinit var pVM: ProductViewModel
    private var networkDataSource: NetworkDataSourceImpl? = null
    var chips: ArrayList<Chip>? = ArrayList()
    private lateinit var layout: LinearLayout

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
        chip.chipStrokeWidth = 1F
        chip.chipIcon = R.drawable.ingredient.toDrawable()


        if (!ingredient.ingredients.isNullOrEmpty()) {
            for (i in ingredient.ingredients!!) {
                preorder(ingredient);
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)
        val scale = resources.displayMetrics.density

        val apiService = ApiService(ConnectivityInterceptorImpl(this))
        networkDataSource = NetworkDataSourceImpl(apiService)
        networkDataSource?.ingredient?.observe(this, Observer {
            val intent = Intent(this, IngredientActivity::class.java)
            intent.putExtra("INGREDIENT", it)
            startActivity(intent)
        })

        this.productToShow = intent?.extras?.get("PRODUCT") as ProductModel
        this.pVM = ViewModelProviders.of(this).get(ProductViewModel::class.java)

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
            this.startActivity(
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

//        logics with info text views

        this.layout = findViewById(R.id.info_product_layout)
        info_product_name.text = productToShow.name
        // when the short version of the product is obtained
        if (productToShow.contents == "") {
//            contents
            info_product_ingredients_temp_text_view.text = "Информация недоступна."
            info_product_manufacturer.text = "Информация недоступна."
            info_product_description.text = "Информация недоступна."
            info_product_category_URL.text = "Информация недоступна."
            info_product_mass.text = "Информация недоступна."
            info_product_bestbefore.text = "Информация недоступна."
            info_product_nutrition_facts.text = "Информация недоступна."
        } else {
            if (productToShow.manufacturer != "NULL") {
                info_product_manufacturer.text = productToShow.manufacturer
            } else {
                info_product_manufacturer.text = "Информация недоступна."
            }
            if (productToShow.description != "NULL") {
                info_product_description.text = productToShow.description
            } else {
                info_product_description.text = "Информация недоступна."
            }
            if (productToShow.categoryURL != "NULL") {
                info_product_category_URL.text = productToShow.categoryURL
            } else {
                info_product_category_URL.text = "Информация недоступна."
            }
            if (productToShow.mass != "NULL") {
                info_product_mass.text = productToShow.mass
            } else {
                info_product_mass.text = "Информация недоступна."
            }
            if (productToShow.bestBefore != "NULL") {
                info_product_bestbefore.text = productToShow.bestBefore
            } else {
                info_product_bestbefore.text = "Информация недоступна."
            }
            if (productToShow.nutrition != "NULL") {
                info_product_nutrition_facts.text = productToShow.nutrition
            } else {
                info_product_nutrition_facts.text = "Информация недоступна."
            }
        }

        if (productToShow.ingredients.isNullOrEmpty()) {
            if (productToShow.contents != null || productToShow.contents != "" || productToShow.contents != "NULL") {
                val layout_contents = LinearLayout(this)
                layout_contents.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
                )
                layout_contents.orientation = LinearLayout.VERTICAL

//                ???
                val layoutPadding = (15 * scale + 0.5f).toInt()

                layout_contents.setPadding(
                    layoutPadding,
                    layoutPadding,
                    layoutPadding,
                    layoutPadding
                )
                this.layout.addView(layout_contents)

                val contentsLabelTextView = TextView(this)
                contentsLabelTextView.text = "Текст состава:"

                val contentsTextView = TextView(this)

//                ???
                val textViewPadding = (8 * scale + 0.5f).toInt()
                contentsTextView.setPadding(0, textViewPadding, 0, textViewPadding)
                contentsTextView.text = productToShow.contents

                layout.addView(contentsLabelTextView)
                layout.addView(contentsTextView)
            }
        } else {
//            for (i in productToShow.ingredients!!) {
//                preorder(i)
//            }
        }

//        if (this.chips != null) {
//            for (i in this.chips!!) {
//               info_ingredient_chips.addView(i)
//            }
//        }
    }
}
