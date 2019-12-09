package com.darx.foodwise


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.speech.tts.TextToSpeech
import android.os.Bundle
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.darx.foodwise.database.*
import com.darx.foodwise.database.IngredientExtended
import com.darx.foodwise.services.ApiService
import com.darx.foodwise.services.ConnectivityInterceptorImpl
import com.darx.foodwise.services.NetworkDataSourceImpl
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.activity_product.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.product_item.*
import kotlinx.android.synthetic.main.product_items.*
import java.util.*
import android.view.ViewStub
import com.darx.foodwise.fragments.EmptyFragment
import com.google.gson.Gson
import kotlinx.android.synthetic.main.product_bottom_sheet.*




class ProductActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private lateinit var productToShow: ProductModel
    private lateinit var pVM: ProductViewModel
    private lateinit var iVM: IngredientViewModel
    private lateinit var gVM: GroupViewModel

    private var groupsDB: List<GroupModel> = listOf()
    private var ingredientsDB: List<IngredientModel> = listOf()

    var chips: ArrayList<Chip>? = ArrayList()
    var voted: Boolean = false

    private var speaker: ImageButton? = null
    private var tts: TextToSpeech? = null

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val locale = Locale("ru")
            val result = tts!!.setLanguage(locale)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS","The Language specified is not supported!")
            } else {
                speaker!!.isEnabled = true
            }

        } else {
            Log.e("TTS", "Initilization Failed!")
        }
    }

    private fun speakOut(text: String) {
        tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null,"")
    }

    private fun pause() {
        tts!!.playSilentUtterance(10,TextToSpeech.QUEUE_FLUSH,"")
    }

    override fun onDestroy() {
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
        super.onDestroy()
    }

    private fun draw() {
        this.chips?.clear()
        if (productToShow.ingredients != null) {
            for (ingredient in productToShow.ingredients!!) {
                addItems(ingredient, false)
            }
        }
        if (info_ingredient_chips != null) {
            info_ingredient_chips.removeAllViews()
        }
        if (this.chips != null) {
            for (i in this.chips!!) {
                info_ingredient_chips.addView(i)
            }
        }

        if (productToShow.ok == 0) {
            info_product_name.setTextColor(getColor(R.color.black))
            info_product_card.setCardBackgroundColor(getColor(R.color.positiveColor))
        } else {
            info_product_name.setTextColor(getColor(R.color.white))
            info_product_card.setCardBackgroundColor(getColor(R.color.negativeColor))
        }
    }


    private fun addItems(ingredient: IngredientExtended, showDanger: Boolean) {
        if (ingredient.ingredients != null) {
            for (ingr in ingredient.ingredients!!) {
                addItems(ingr, showDanger)
            }
        }

        val chip = Chip(this)
        chip.isClickable = true

        if (ingredient.ok) {
            chip.setTextAppearanceResource(R.style.ChipTextStyle_positive)
            chip.setChipBackgroundColorResource(R.color.positiveColor)
            chip.setChipIconResource(R.drawable.ic_checkmark_black)
        } else {
            chip.setTextAppearanceResource(R.style.ChipTextStyle_negative)
            chip.setChipBackgroundColorResource(R.color.negativeColor)
            chip.setChipIconResource(R.drawable.ic_stop_white)
        }


        chip.setOnClickListener {
            var ingr = IngredientModel(ingredient)
            if (!ingredient.ok) {
                if (ingredient.groupMached) {
                    ingr.allowed = true
                    iVM.add_(ingr)
                } else {
                    iVM.deleteOne_(ingr)
                }
            } else {
                if (ingredient.groupMached) {
                    iVM.deleteOne_(ingr)
                } else {
                    ingr.allowed = false
                    iVM.add_(ingr)
                }
            }
        }

        if (ingredient.name != "") {
            chip.text = ingredient.name
            this.chips?.add(chip)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)

        setSupportActionBar(product_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        this.pVM = ViewModelProviders.of(this).get(ProductViewModel::class.java)
        this.iVM = ViewModelProviders.of(this).get(IngredientViewModel::class.java)
        this.gVM = ViewModelProviders.of(this).get(GroupViewModel::class.java)

        var spoke = false
        speaker = findViewById(R.id.info_speaker_ib)
        speaker!!.setBackgroundResource(R.drawable.ic_speaker_on_black)

        tts = TextToSpeech(this, this)

        tts!!.setOnUtteranceProgressListener(object : UtteranceProgressListener(){
                override fun onDone(utteranceId: String?) {
                    speaker!!.setBackgroundResource(R.drawable.ic_speaker_on_black)
                }
                override fun onError(utteranceId: String?) {}
                override fun onStart(utteranceId: String?) {}
            }
        )

        this.productToShow = intent?.extras?.get("PRODUCT") as ProductModel

        if (this.productToShow.categoryURL == "Fruit") {
//            container.removeAllViews()
            info_product_layout.removeAllViews()

            val fruitView = findViewById<LinearLayout>(R.id.fruit_stub)
            fruitView.visibility = View.VISIBLE

            fruit_name.text = this.productToShow.name
            info_product_name.text = productToShow.name
            if (!productToShow.image.isNullOrEmpty() || productToShow.image == "NULL") {
                Picasso.get().load(productToShow.image).error(R.drawable.ic_cereals__black)
                    .into(info_product_image);
            } else {
                info_product_image.setImageResource(R.drawable.ic_cereals__black)
            }

            val nutritionFacts = Gson().fromJson(this.productToShow.nutrition, Array<Array<String>>::class.java)
            fruit_container_1_name.text = nutritionFacts[0][0]
            fruit_container_1_value.text = nutritionFacts[1][0]
            fruit_container_2_name.text = nutritionFacts[0][1]
            fruit_container_2_value.text = nutritionFacts[1][1]
            fruit_container_3_name.text = nutritionFacts[0][2]
            fruit_container_3_value.text = nutritionFacts[1][2]
            fruit_container_4_name.text = nutritionFacts[0][3]
            fruit_container_4_value.text = nutritionFacts[1][3]

            fruit_description.text = this.productToShow.description

            val vitamins = Gson().fromJson(this.productToShow.contents, Array<Array<String>>::class.java)

            fruit_vitamin_1_name.text = vitamins[0][0]
            fruit_vitamin_1_value.text = vitamins[1][0]
            fruit_vitamin_2_name.text = vitamins[0][1]
            fruit_vitamin_2_value.text = vitamins[1][1]
            fruit_vitamin_3_name.text = vitamins[0][2]
            fruit_vitamin_3_value.text = vitamins[1][2]
            fruit_vitamin_4_name.text = vitamins[0][3]
            fruit_vitamin_4_value.text = vitamins[1][3]
            fruit_vitamin_5_name.text = vitamins[0][4]
            fruit_vitamin_5_value.text = vitamins[1][4]
            fruit_vitamin_6_name.text = vitamins[0][5]
            fruit_vitamin_6_value.text = vitamins[1][5]

            fruit_feedback_container.visibility = View.GONE
        } else {
            gVM.getAll_().observe(this, Observer {
                groupsDB = it
                if (productToShow != null) {
                    filter()
                    draw()
                }
            })
            iVM.getAll_().observe(this, Observer<List<IngredientModel>> {
                ingredientsDB = it
                if (productToShow != null) {
                    filter()
                    draw()
                }
            })

            if (this.productToShow.contents.isNullOrEmpty() || this.productToShow.contents == "NULL") {
                speaker!!.isEnabled = false
                speaker!!.visibility = View.GONE
            } else {
                speaker!!.visibility = View.VISIBLE
            }

            if (speaker!!.visibility != View.GONE) {
                speaker!!.setOnClickListener {
                    if (spoke) {
                        speaker!!.setBackgroundResource(R.drawable.ic_speaker_on_black)
                        pause()
                        spoke = false
                    } else {
                        speaker!!.setBackgroundResource(R.drawable.ic_speaker_off_black)
                        speakOut(this.productToShow.contents!!)
                        spoke = true
                    }
                }
            }

//        Setting correct views for 2 types of products
            info_product_name.text = productToShow.name

            if (!productToShow.image.isNullOrEmpty() || productToShow.image == "NULL") {
                Picasso.get().load(productToShow.image).error(R.drawable.ic_cereals__black)
                    .into(info_product_image);
            } else {
                info_product_image.setImageResource(R.drawable.ic_cereals__black)
            }

            // short version of the product
            if (productToShow.shrinked == true) {
                info_product_layout.removeAllViews()
                info_product_layout.visibility = GONE
                showEmptyFragment()
                product_fragments_frame.visibility = VISIBLE
            } else if (productToShow.shrinked == false) {
                if (productToShow.contents == "NULL" || productToShow.contents == "") {
                    info_contents_container.visibility = View.GONE
                } else {
                    info_product_contents.text = productToShow.contents
//                var collapsed = CollapseUtils(this, hide, info_product_contents)
//                collapsed.initDescription(productToShow.contents!!)
            }
            if (productToShow.manufacturer == "NULL" || productToShow.manufacturer == "") {
                info_manufacturer_container.visibility = View.GONE
            } else {
                info_product_manufacturer.text = productToShow.manufacturer
            }
            if (productToShow.description == "NULL" || productToShow.description == "") {
                info_description_container.visibility = View.GONE
            } else {
                info_product_description.text = productToShow.description
            }
            if (productToShow.categoryURL == "NULL" || productToShow.categoryURL == "") {
                info_category_url_container.visibility = View.GONE
            } else {
                info_product_category_URL.text = productToShow.categoryURL
            }
            if (productToShow.mass == "NULL" || productToShow.mass == "") {
                info_mass_container.visibility = View.GONE
            } else {
                info_product_mass.text = productToShow.mass
            }
            if (productToShow.bestBefore == "NULL" || productToShow.bestBefore == "") {
                info_best_before_container.visibility = View.GONE
            } else {
                info_product_bestbefore.text = productToShow.bestBefore
            }
            if (productToShow.nutrition == "NULL" || productToShow.nutrition == "") {
                info_nutrition_facts_container.visibility = View.GONE
            } else {
                info_product_nutrition_facts.text = productToShow.nutrition
            }
            if (!productToShow.ingredients.isNullOrEmpty()) {
                if (productToShow != null) {
                    filter()
                    draw()
                }
            } else {
                    info_ingredients_container.visibility = View.GONE
                    info_feedback_container.visibility = View.GONE
                }

                if (info_feedback_container.visibility == View.VISIBLE) {
                    good.setOnClickListener {
                        if (voted == false) {
                            Toast.makeText(
                                this, "Спасибо за отзыв!",
                                Toast.LENGTH_SHORT
                            ).show()
                            voted = true
                        } else {
                            Toast.makeText(
                                this, "Вы уже проголосовали!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    bad.setOnClickListener {
                        if (voted == false) {
                            Toast.makeText(
                                this, "Спасибо за отзыв!",
                                Toast.LENGTH_SHORT
                            ).show()
                            voted = true
                        } else {
                            Toast.makeText(
                                this, "Вы уже проголосовали!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            R.id.action_share -> {
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
                return true
            }
            R.id.action_favourite -> {
                productToShow.starred = !productToShow.starred
                if (productToShow.starred) {
                    pVM.upsert_(productToShow)
                } else {
                    if (productToShow.scanned) {
                        pVM.upsert_(productToShow)
                    } else {
                        pVM.deleteOne_(productToShow)
                    }
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.product_toolbar, menu)

        val item = menu!!.findItem(R.id.action_favourite)

        pVM.getOne_(productToShow.barcode)?.observe(this,
            Observer<ProductModel> { t ->
                if (t != null && t.starred) {
                    item.setIcon(R.drawable.ic_star_yellow)
                } else {
                    item.setIcon(R.drawable.ic_star_white)
                }
            })

        return true
    }

    private fun filter() {
        productToShow.ok = 0
        if (productToShow.ingredients != null) {
            for (ingr in productToShow.ingredients!!) {
                cleanInfo(ingr)
            }
        }

        if (productToShow.ingredients != null) {
            for (ingredient in productToShow.ingredients!!) {
                productToShow.ok += preorder(ingredient)
            }
        }
    }

    private fun preorder(ingredient: IngredientExtended): Int {
        var count: Int = 0
        if (ingredient.ingredients != null) {
            for (ingr in ingredient.ingredients!!) {
                count += preorder(ingr)
            }
        }


        if (ingredient.groups != null) {
            for (g in ingredient.groups) {
                for (group in groupsDB) {
                    if (group.id == g) {
                        ingredient.groupMached = true
                        ingredient.ok = false
                    }
                }
            }
        }
        for (ingredientDB in ingredientsDB) {
            if (ingredientDB.id == ingredient.id) {
                ingredient.allowed = ingredientDB.allowed!!
                ingredient.ok = ingredient.allowed
            }
        }
        return count + if (!ingredient.ok) 1 else 0
    }

    private fun cleanInfo(ingredient: IngredientExtended) {
        if (ingredient.ingredients != null) {
            for (ingr in ingredient.ingredients!!) {
                cleanInfo(ingr)
            }
        }
        ingredient.ok = true
        ingredient.allowed = true
        ingredient.groupMached = false
    }

    private fun showEmptyFragment() {
        val emptyFragment = EmptyFragment(
            R.drawable.empty_product_info,
            getString(R.string.empty_product_message),
            getString(R.string.empty_product_button),
            LinearLayout.VERTICAL,
            View.OnClickListener {}
        )
        supportFragmentManager.beginTransaction().replace(R.id.product_fragments_frame, emptyFragment).commit()
    }
}
