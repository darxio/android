package com.darx.foodscaner


import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.speech.tts.TextToSpeech
import android.os.Bundle
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
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
import androidx.core.view.isVisible
import java.io.IOException
import java.net.HttpURLConnection
import java.util.*


class ProductActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private lateinit var productToShow: ProductModel
    private lateinit var pVM: ProductViewModel
    private lateinit var iVM: IngredientViewModel
    private lateinit var gVM: GroupViewModel
    private var networkDataSource: NetworkDataSourceImpl? = null
    var chips: ArrayList<Chip>? = ArrayList()
    private lateinit var layout: LinearLayout
    var ok: Boolean = true
    var cautious: Boolean = false

    private var speaker: ImageButton? = null
    private var tts: TextToSpeech? = null

    val states = arrayOf(
        intArrayOf(android.R.attr.state_enabled), // enabled
        intArrayOf(-android.R.attr.state_enabled), // disabled
        intArrayOf(-android.R.attr.state_checked), // unchecked
        intArrayOf(android.R.attr.state_pressed)  // pressed
    )

    val positiveColors = intArrayOf(R.color.positiveColor, R.color.positiveColor, R.color.positiveColor, R.color.positiveColor)
    val negativeColors = intArrayOf(R.color.negativeColor, R.color.negativeColor, R.color.negativeColor, R.color.negativeColor)

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // set US English as language for tts
            val result = tts!!.setLanguage(Locale.US)

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
        // Shutdown TTS
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
        super.onDestroy()
    }

    private fun preorder(ingredient: IngredientExtended?) {
        if (ingredient == null) {
            return
        }

        // logics
        val chip = Chip(this)
        if (ingredient.name != "") {
            chip.text = ingredient.name
        }

        chip.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                networkDataSource!!.getIngredientByID(ingredient.id)
            }
        }

        if (iVM != null) {
            iVM?.getOne_(ingredient.id)
                ?.observe(this@ProductActivity, object : Observer<IngredientModel> {
                    override fun onChanged(t: IngredientModel?) {
                        if (t?.id == ingredient.id) {
                            chip.setChipBackgroundColorResource(R.drawable.bg_chip_state_list_negative)
                            this@ProductActivity.ok = false
                        } else {
                            if (ingredient.danger!! > 0) {
                                chip.setChipBackgroundColorResource(R.drawable.bg_chip_state_list_cautious)
                                this@ProductActivity.cautious = true
                            } else {
                                chip.setChipBackgroundColorResource(R.drawable.bg_chip_state_list_positive)
                            }
                        }
                    }
                })
        }

        chip.isClickable = true
        if (chip.text != "") {
            this.chips?.add(chip)
        }

        if (!ingredient.ingredients.isNullOrEmpty()) {
            for (i in ingredient.ingredients!!) {
                preorder(i)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)
        val scale = resources.displayMetrics.density
        var spoke = false
        info_speaker_ib.setBackgroundResource(R.drawable.ic_speaker)

        speaker = findViewById(R.id.info_speaker_ib)

        speaker!!.isEnabled = false;
        tts = TextToSpeech(this, this)

        tts!!.setOnUtteranceProgressListener(object : UtteranceProgressListener(){
                override fun onDone(utteranceId: String?) {
                    info_speaker_ib.setBackgroundResource(R.drawable.ic_speaker)
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

        if (this.productToShow.contents.isNullOrEmpty()) {
            speaker!!.isEnabled == false
            speaker!!.visibility = View.INVISIBLE
        } else {
            speaker!!.visibility = View.VISIBLE
        }

        speaker!!.setOnClickListener {
            if (spoke) {
                info_speaker_ib.setBackgroundResource(R.drawable.ic_speaker)
                pause()
                spoke = false
            } else {
                info_speaker_ib.setBackgroundResource(R.drawable.ic_pause)
                speakOut(this.productToShow.contents!!)
                spoke = true
            }
        }

        this.pVM = ViewModelProviders.of(this).get(ProductViewModel::class.java)
        this.iVM = ViewModelProviders.of(this).get(IngredientViewModel::class.java)
        this.gVM = ViewModelProviders.of(this).get(GroupViewModel::class.java)

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

//        try {
//            val url = java.net.URL(productToShow.image)
//            val connection = url.openConnection() as HttpURLConnection
//            connection.setDoInput(true)
//            connection.connect()
//            val input = connection.getInputStream()
//            info_product_image.setImageBitmap(BitmapFactory.decodeStream(input))
//        } catch (e: IOException) {
//            Log.e("Exception", "image downloaded failed.")
//        }

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
            for (i in productToShow.ingredients!!) {
                preorder(i)
            }
        }

        if (this.chips != null) {
            for (i in this.chips!!) {
               info_ingredient_chips.addView(i)
            }
        }

        if (ok == false) {
            info_material_card.setBackgroundColor(R.color.negativeColor)
            info_product_warning_text.text = "Cодержит ингредиенты, которые вы не хотите есть!"
        }
    }
}
