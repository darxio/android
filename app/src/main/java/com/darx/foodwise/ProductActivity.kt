package com.darx.foodwise


import android.content.Intent
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.speech.tts.TextToSpeech
import android.os.Bundle
import android.provider.Contacts.PresenceColumns.INVISIBLE
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.*
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.darx.foodwise.database.*
import com.darx.foodwise.database.IngredientExtended
import com.darx.foodwise.services.ApiService
import com.darx.foodwise.services.ConnectivityInterceptorImpl
import com.darx.foodwise.services.NetworkDataSourceImpl
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.android.synthetic.main.activity_product.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import com.squareup.picasso.Picasso
import java.util.*


class ProductActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private lateinit var productToShow: ProductModel
    private lateinit var pVM: ProductViewModel
    private lateinit var iVM: IngredientViewModel
    private lateinit var gVM: GroupViewModel
    private var networkDataSource: NetworkDataSourceImpl? = null

    var chips: ArrayList<Chip>? = ArrayList()
    var warningLevel: Int = 0
    var ok: Int = 0
    var voted: Boolean = false

    private var speaker: ImageButton? = null
    private var tts: TextToSpeech? = null

//    private var isAllowed: Boolean = true

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

    fun checkStatus(ingredient: IngredientModel?, isGroupsMatched: Boolean): Boolean {
        var isAllowed = true
        if (isGroupsMatched) {
            isAllowed = (ingredient != null && ingredient.allowed!!)
        } else {
            if (ingredient != null) {
                isAllowed = ingredient.allowed!!
            }
        }
        return isAllowed
    }

    private fun preorder(ingredient: IngredientExtended?, showDanger: Boolean) {
        if (ingredient == null) {
            return
        }

        val chip = Chip(this)
        if (ingredient.name != "") {
            chip.text = ingredient.name
        }

        var isGroupsMatched = false

        if (ingredient.groups.isNullOrEmpty()) {
            ingredient.groups = ArrayList()
        }

        var isAllowed = true
        gVM.checkAll_(ingredient.groups)?.observe(this@ProductActivity, object : Observer<Boolean> {
            override fun onChanged(t: Boolean?) {
                isGroupsMatched = t ?: false
                isAllowed = checkStatus(null, isGroupsMatched)
                if (isAllowed) {
                    chip.setChipBackgroundColorResource(R.drawable.bg_chip_state_list_positive)
                } else {
                    chip.setChipBackgroundColorResource(R.drawable.bg_chip_state_list_negative)
                }
                if (ok > 0) {
                    info_product_card.setCardBackgroundColor(getColorStateList(R.color.negativeColor))
                } else {
                    info_product_card.setCardBackgroundColor(getColorStateList(R.color.positiveColor))
                }
            }
        })

        iVM.getOne_(ingredient.id)?.observe(this@ProductActivity, object : Observer<IngredientModel> {
            override fun onChanged(t: IngredientModel?) {
                isAllowed = checkStatus(t, isGroupsMatched)
                if (isAllowed) {
                    chip.setChipBackgroundColorResource(R.drawable.bg_chip_state_list_positive)
                    if (ingredient.danger!! > 1 && showDanger) {
                        chip.setChipBackgroundColorResource(R.drawable.bg_chip_state_list_cautious)
                    }
                } else {
                    chip.setChipBackgroundColorResource(R.drawable.bg_chip_state_list_negative)
                    ok++
                }
                if (ok > 0) {
                    info_product_card.setCardBackgroundColor(getColorStateList(R.color.negativeColor))
                } else {
                    info_product_card.setCardBackgroundColor(getColorStateList(R.color.positiveColor))
                }
            }
        })


        chip.isClickable = true
        if (chip.text != "") {
            this.chips?.add(chip)
        }

        if (!ingredient.ingredients.isNullOrEmpty()) {
            for (i in ingredient.ingredients!!) {
                preorder(i, showDanger)
            }
        }

        chip.setOnClickListener {
            val ingr = IngredientModel(ingredient)
            if (!isAllowed) {
                ok--
                if (isGroupsMatched) {
                    ingr.allowed = true
                    iVM.add_(ingr)
                } else {
                    iVM.deleteOne_(ingr)
                }
            } else {
                if (isGroupsMatched) {
                    iVM.deleteOne_(ingr)
                } else {
                    ingr.allowed = false
                    iVM.add_(ingr)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)

        setSupportActionBar(product_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

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

        val apiService = ApiService(ConnectivityInterceptorImpl(this))
        networkDataSource = NetworkDataSourceImpl(apiService, this)

        networkDataSource?.ingredient?.observe(this, Observer {
            val intent = Intent(this, IngredientActivity::class.java)
            intent.putExtra("INGREDIENT", it)
            startActivity(intent)
        })

        this.productToShow = intent?.extras?.get("PRODUCT") as ProductModel

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

        this.pVM = ViewModelProviders.of(this).get(ProductViewModel::class.java)
        this.iVM = ViewModelProviders.of(this).get(IngredientViewModel::class.java)
        this.gVM = ViewModelProviders.of(this).get(GroupViewModel::class.java)

//        Setting correct views for 2 types of products
        info_product_name.text = productToShow.name

        if (!productToShow.image.isNullOrEmpty() || productToShow.image == "NULL") {
            Picasso.get().load(productToShow.image).error(R.drawable.ic_cereals__black).into(info_product_image);
        } else {
            info_product_image.setImageResource(R.drawable.ic_cereals__black)
        }

        // short version of the product
        if (productToShow.shrinked == true) {
            info_product_layout.removeAllViews()

            // to do -- empty fragment here

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
                for (i in productToShow.ingredients!!) {
                    if (i.name.isNotEmpty()) {
                        preorder(i, true)
                    }
                }

                if (this.chips != null) {
                    for (i in this.chips!!) {
                        info_ingredient_chips.addView(i)
                    }
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

        pVM.getOne_(productToShow.barcode)?.observe(this, object : Observer<ProductModel> {
            override fun onChanged(t: ProductModel?) {
                if (t != null && t.starred) {
                    item.setIcon(R.drawable.ic_star_yellow)
                } else {
                    item.setIcon(R.drawable.ic_star_white)
                }
            }
        })

        return true
    }
}
