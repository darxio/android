package com.darx.foodscaner


import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.speech.tts.TextToSpeech
import android.os.Bundle
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.view.View
import android.view.View.INVISIBLE
import android.widget.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.darx.foodscaner.database.*
import com.darx.foodscaner.database.IngredientExtended
import com.darx.foodscaner.fragments.FeedbackFragment
import com.darx.foodscaner.services.ApiService
import com.darx.foodscaner.services.ConnectivityInterceptorImpl
import com.darx.foodscaner.services.NetworkDataSourceImpl
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.activity_product.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import com.squareup.picasso.Picasso
import okhttp3.*
import java.io.IOException
import java.util.*


class ProductActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private lateinit var productToShow: ProductModel
    private lateinit var pVM: ProductViewModel
    private lateinit var iVM: IngredientViewModel
    private lateinit var gVM: GroupViewModel
    private var networkDataSource: NetworkDataSourceImpl? = null
    var chips: ArrayList<Chip>? = ArrayList()
    private lateinit var layout: LinearLayout
//    var ok: Boolean = true

    private var speaker: ImageButton? = null
    private var tts: TextToSpeech? = null

    private var isAllowed: Boolean = true
//    private var isGroupsMatched: Boolean = false

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

    fun checkStatus(ingredient: IngredientModel?, isGroupsMatched: Boolean): Boolean {
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

    private fun preorder(ingredient: IngredientExtended?) {
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
        gVM.checkAll_(ingredient.groups)?.observe(this@ProductActivity, object : Observer<Boolean> {
            override fun onChanged(t: Boolean?) {
                isGroupsMatched = t ?: false
                if (checkStatus(null, isGroupsMatched)) {
                    chip.setChipBackgroundColorResource(R.drawable.bg_chip_state_list_positive)
                } else {
                    chip.setChipBackgroundColorResource(R.drawable.bg_chip_state_list_negative)
                }
            }
        })

        iVM.getOne_(ingredient.id)?.observe(this@ProductActivity, object : Observer<IngredientModel> {
            override fun onChanged(t: IngredientModel?) {
                if (checkStatus(t, isGroupsMatched)) {
                    chip.setChipBackgroundColorResource(R.drawable.bg_chip_state_list_positive)
                } else {
                    chip.setChipBackgroundColorResource(R.drawable.bg_chip_state_list_negative)
                }
            }
        })


        chip.isClickable = true
        if (chip.text != "") {
            this.chips?.add(chip)
        }

        if (!ingredient.ingredients.isNullOrEmpty()) {
            for (i in ingredient.ingredients!!) {
                preorder(i)
            }
        }

        chip.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                networkDataSource!!.getIngredientByID(ingredient.id)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)
        var spoke = false
        info_speaker_ib.setBackgroundResource(R.drawable.ic_speaker)

        speaker = findViewById(R.id.info_speaker_ib)

        speaker!!.isEnabled = false
        tts = TextToSpeech(this, this)

        tts!!.setOnUtteranceProgressListener(object : UtteranceProgressListener(){
                override fun onDone(utteranceId: String?) {
                    info_speaker_ib.setBackgroundResource(R.drawable.ic_speaker)
                }

                override fun onError(utteranceId: String?) {
                    Log.d("TTS","OnError: something went wrong")
                }

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

        val starred = findViewById<ImageButton>(R.id.info_starred_ib)
        val share = findViewById<ImageButton>(R.id.info_share_btn)
        val back = findViewById<Button>(R.id.back_btn)

        // logics with image buttons
        pVM.getOne_(productToShow.barcode)?.observe(this, object : Observer<ProductModel> {
            override fun onChanged(t: ProductModel?) {
                if (t != null && t.starred) {
                    starred.setBackgroundResource(R.drawable.ic_starred)
                } else {
                    starred.setBackgroundResource(R.drawable.ic_unstarred)
                }
            }
        })

        starred.setOnClickListener {
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

        if (!productToShow.image.isNullOrEmpty() || productToShow.image == "NULL") {
            Picasso.get().load(productToShow.image).error(R.drawable.product).into(info_product_image);
        } else {
            info_product_image.setImageResource(R.drawable.product)
        }

        // when the short version of the product is obtained
        if (productToShow.contents == "") {
            info_product_ingredients_temp_text_view.text = "Информация недоступна."
            info_product_manufacturer.text = "Информация недоступна."
            info_product_description.text = "Информация недоступна."
            info_product_category_URL.text = "Информация недоступна."
            info_product_mass.text = "Информация недоступна."
            info_product_bestbefore.text = "Информация недоступна."
            info_product_nutrition_facts.text = "Информация недоступна."
            bad.isEnabled = false
            good.isEnabled = false
            bad.visibility = INVISIBLE
            good.visibility = INVISIBLE
        } else {
            if (productToShow.contents != "NULL") {
                info_product_contents.text = productToShow.contents
            } else {
                info_product_contents.text = "Информация недоступна."
            }
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

        if (!productToShow.ingredients.isNullOrEmpty()) {
            for (i in productToShow.ingredients!!) {
                preorder(i)
            }
        }

        if (this.chips != null) {
            for (i in this.chips!!) {
               info_ingredient_chips.addView(i)
            }
        }

        if (good.isEnabled && bad.isEnabled) {
            good.setOnClickListener {
                Toast.makeText(
                    this, "Спасибо за отзыв!",
                    Toast.LENGTH_SHORT
                ).show()
            }

            bad.setOnClickListener {
                // запрос в сеть
                Toast.makeText(
                    this, "Спасибо за отзыв!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

//        if (ok == false) {
//            info_material_card.setBackgroundColor(R.color.negativeColor)
//            info_product_warning_text.text = "Cодержит ингредиенты, которые вы не хотите есть!"
//        }
    }
}
