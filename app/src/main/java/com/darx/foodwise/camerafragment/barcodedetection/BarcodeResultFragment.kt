/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.darx.foodwise.camerafragment.barcodedetection

import android.content.DialogInterface
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.darx.foodwise.R
import com.darx.foodwise.camerafragment.camera.WorkflowModel
import com.darx.foodwise.camerafragment.camera.WorkflowModel.WorkflowState
import com.darx.foodwise.database.*
import com.darx.foodwise.database.IngredientExtended
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import java.util.*

/** Displays the bottom sheet to present barcode fields contained in the detected barcode.  */
class BarcodeResultFragment : BottomSheetDialogFragment(), TextToSpeech.OnInitListener {

//    ******************************************

    private lateinit var product_toolbar_ :  androidx.appcompat.widget.Toolbar
    private lateinit var info_product_name_ :  TextView
    private lateinit var info_product_image_ :  ImageView
    private lateinit var info_product_layout_ :  LinearLayout
    private lateinit var info_product_contents_ :  TextView
    private lateinit var info_contents_container_ :  LinearLayout
    private lateinit var info_product_manufacturer_ :  TextView
    private lateinit var info_manufacturer_container_ :  LinearLayout
    private lateinit var info_product_description_ :  TextView
    private lateinit var info_description_container_ :  LinearLayout
    private lateinit var info_product_mass_ :  TextView
    private lateinit var info_mass_container_ :  LinearLayout
    private lateinit var info_product_category_URL_ :  TextView
    private lateinit var info_category_url_container_ :  LinearLayout
    private lateinit var info_product_bestbefore_ :  TextView
    private lateinit var info_best_before_container_ :  LinearLayout
    private lateinit var info_product_nutrition_facts_ :  TextView
    private lateinit var info_nutrition_facts_container_ :  LinearLayout
    private lateinit var info_ingredient_chips_ :  ChipGroup
    private lateinit var info_ingredients_container_ :  LinearLayout
    private lateinit var info_feedback_container_ :  LinearLayout
    private lateinit var good_ :  ImageButton
    private lateinit var bad_ :  ImageButton
    private lateinit var info_product_card_ : MaterialCardView

    private lateinit var fruit_name_ : TextView
    private lateinit var fruit_container_1_name_ : TextView
    private lateinit var fruit_container_1_value_  : TextView
    private lateinit var fruit_container_2_name_  : TextView
    private lateinit var fruit_container_2_value_  : TextView
    private lateinit var fruit_container_3_name_  : TextView
    private lateinit var fruit_container_3_value_  : TextView
    private lateinit var fruit_container_4_name_  : TextView
    private lateinit var fruit_container_4_value_  : TextView
    private lateinit var fruit_description_ : TextView
    private lateinit var fruit_vitamin_1_name_ : TextView
    private lateinit var fruit_vitamin_1_value_ : TextView
    private lateinit var fruit_vitamin_2_name_ : TextView
    private lateinit var fruit_vitamin_2_value_ : TextView
    private lateinit var fruit_vitamin_3_name_ : TextView
    private lateinit var fruit_vitamin_3_value_ : TextView
    private lateinit var fruit_vitamin_4_name_ : TextView
    private lateinit var fruit_vitamin_4_value_ : TextView
    private lateinit var fruit_vitamin_5_name_ : TextView
    private lateinit var fruit_vitamin_5_value_ : TextView
    private lateinit var fruit_vitamin_6_name_ : TextView
    private lateinit var fruit_vitamin_6_value_ : TextView
    private lateinit var fruit_feedback_container_ : LinearLayout

//    ******************************************


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
        tts!!.playSilentUtterance(10, TextToSpeech.QUEUE_FLUSH,"")
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
        if (info_ingredient_chips_ != null) {
            info_ingredient_chips_.removeAllViews()
        }
        if (this.chips != null) {
            for (i in this.chips!!) {
                info_ingredient_chips_.addView(i)
            }
        }

        if (productToShow.ok == 0) {
            info_product_name_.setTextColor(context!!.getColor(R.color.black))
            info_product_card_.setCardBackgroundColor(context!!.getColor(R.color.positiveColor))
        } else {
            info_product_name_.setTextColor(context!!.getColor(R.color.white))
            info_product_card_.setCardBackgroundColor(context!!.getColor(R.color.negativeColor))
        }
    }

    private fun addItems(ingredient: IngredientExtended, showDanger: Boolean) {
        if (ingredient.ingredients != null) {
            for (ingr in ingredient.ingredients!!) {
                addItems(ingr, showDanger)
            }
        }

        val chip = Chip(context!!)
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

// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

    var barcodeField: BarcodeField? = null

    override fun onCreateView(
        layoutInflater: LayoutInflater,
        viewGroup: ViewGroup?,
        bundle: Bundle?
    ): View {
        val root: View = layoutInflater.inflate(R.layout.barcode_bottom_sheet, viewGroup, false)

//        ALLOCATION OF VIEW ELEMENTS - START

        product_toolbar_ = root.findViewById<androidx.appcompat.widget.Toolbar>(R.id.product_toolbar)
        product_toolbar_.visibility = View.GONE
        info_product_name_ = root.findViewById<TextView>(R.id.info_product_name)
        info_product_image_ = root.findViewById<ImageView>(R.id.info_product_image)
        info_product_layout_ = root.findViewById<LinearLayout>(R.id.info_product_layout)
        info_product_contents_ = root.findViewById<TextView>(R.id.info_product_contents)
        info_contents_container_ = root.findViewById<LinearLayout>(R.id.info_contents_container)
        info_product_manufacturer_ = root.findViewById<TextView>(R.id.info_product_manufacturer)
        info_manufacturer_container_ = root.findViewById<LinearLayout>(R.id.info_manufacturer_container)
        info_product_description_ = root.findViewById<TextView>(R.id.info_product_description)
        info_description_container_ = root.findViewById<LinearLayout>(R.id.info_description_container)
        info_product_mass_ = root.findViewById<TextView>(R.id.info_product_mass)
        info_mass_container_ = root.findViewById<LinearLayout>(R.id.info_mass_container)
        info_product_category_URL_ = root.findViewById<TextView>(R.id.info_product_category_URL)
        info_category_url_container_ = root.findViewById<LinearLayout>(R.id.info_category_url_container)
        info_product_bestbefore_ = root.findViewById<TextView>(R.id.info_product_bestbefore)
        info_best_before_container_ = root.findViewById<LinearLayout>(R.id.info_best_before_container)
        info_product_nutrition_facts_ = root.findViewById<TextView>(R.id.info_product_nutrition_facts)
        info_nutrition_facts_container_ = root.findViewById<LinearLayout>(R.id.info_nutrition_facts_container)
        info_ingredient_chips_ = root.findViewById<ChipGroup>(R.id.info_ingredient_chips)
        info_ingredients_container_ = root.findViewById<LinearLayout>(R.id.info_ingredients_container)
        info_feedback_container_ = root.findViewById<LinearLayout>(R.id.info_feedback_container)
        good_ = root.findViewById<ImageButton>(R.id.good)
        bad_ = root.findViewById<ImageButton>(R.id.bad)
        speaker = root.findViewById(R.id.info_speaker_ib)


        info_product_card_ = root.findViewById(R.id.info_product_card)


        fruit_name_ = root.findViewById(R.id.fruit_name)
        fruit_container_1_name_ = root.findViewById(R.id.fruit_container_1_name)
        fruit_container_1_value_ = root.findViewById(R.id.fruit_container_1_value)
        fruit_container_2_name_ = root.findViewById(R.id.fruit_container_2_name)
        fruit_container_2_value_ = root.findViewById(R.id.fruit_container_2_value)
        fruit_container_3_name_ = root.findViewById(R.id.fruit_container_3_name)
        fruit_container_3_value_= root.findViewById(R.id.fruit_container_3_value)
        fruit_container_4_name_= root.findViewById(R.id.fruit_container_4_name)
        fruit_container_4_value_= root.findViewById(R.id.fruit_container_4_value)
        fruit_description_= root.findViewById(R.id.fruit_description)
        fruit_vitamin_1_name_= root.findViewById(R.id.fruit_vitamin_1_name)
        fruit_vitamin_1_value_= root.findViewById(R.id.fruit_vitamin_1_value)
        fruit_vitamin_2_name_= root.findViewById(R.id.fruit_vitamin_2_name)
        fruit_vitamin_2_value_= root.findViewById(R.id.fruit_vitamin_2_value)
        fruit_vitamin_3_name_= root.findViewById(R.id.fruit_vitamin_3_name)
        fruit_vitamin_3_value_= root.findViewById(R.id.fruit_vitamin_3_value)
        fruit_vitamin_4_name_= root.findViewById(R.id.fruit_vitamin_4_name)
        fruit_vitamin_4_value_= root.findViewById(R.id.fruit_vitamin_4_value)
        fruit_vitamin_5_name_= root.findViewById(R.id.fruit_vitamin_5_name)
        fruit_vitamin_5_value_= root.findViewById(R.id.fruit_vitamin_5_value)
        fruit_vitamin_6_name_= root.findViewById(R.id.fruit_vitamin_6_name)
        fruit_vitamin_6_value_= root.findViewById(R.id.fruit_vitamin_6_value)
        fruit_feedback_container_= root.findViewById(R.id.fruit_feedback_container)

//        ALLOCATION OF VIEW ELEMENTS - END

        val arguments = arguments
        var barcodeField: BarcodeField =
            if (arguments?.containsKey(ARG_BARCODE_FIELD_LIST) == true) {
                arguments.getSerializable(ARG_BARCODE_FIELD_LIST) as BarcodeField? ?: BarcodeField("", "")
            } else {
                Log.e(TAG, "No barcode field list passed in!")
                BarcodeField("", "")
            }

        this.productToShow = if (arguments?.containsKey(ARG_PRODUCT) == true) {
                    arguments.getSerializable(ARG_PRODUCT) as ProductModel? ?: ProductModel()
        } else {
            Log.e(TAG, "No product passed in!")
            ProductModel()
        }

//        !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!


        this.pVM = ViewModelProviders.of(this).get(ProductViewModel::class.java)
        this.iVM = ViewModelProviders.of(this).get(IngredientViewModel::class.java)
        this.gVM = ViewModelProviders.of(this).get(GroupViewModel::class.java)

        var spoke = false
        speaker!!.setBackgroundResource(R.drawable.ic_speaker_on_black)

        tts = TextToSpeech(context!!, this)

        tts!!.setOnUtteranceProgressListener(object : UtteranceProgressListener(){
            override fun onDone(utteranceId: String?) {
                speaker!!.setBackgroundResource(R.drawable.ic_speaker_on_black)
            }
            override fun onError(utteranceId: String?) {}
            override fun onStart(utteranceId: String?) {}
        }
        )

        if (this.productToShow.categoryURL == "Fruit") {
            info_product_layout_.removeAllViews()

            val fruitView = root.findViewById<LinearLayout>(R.id.fruit_stub)
            fruitView.visibility = View.VISIBLE

            fruit_name_.text = this.productToShow.name
            info_product_name_.text = productToShow.name
            if (!productToShow.image.isNullOrEmpty() || productToShow.image == "NULL") {
                Picasso.get().load(productToShow.image).error(R.drawable.ic_cereals__black)
                    .into(info_product_image_);
            } else {
                info_product_image_.setImageResource(R.drawable.ic_cereals__black)
            }

            val nutritionFacts = Gson().fromJson(this.productToShow.nutrition, Array<Array<String>>::class.java)
            fruit_container_1_name_.text = nutritionFacts[0][0]
            fruit_container_1_value_.text = nutritionFacts[1][0]
            fruit_container_2_name_.text = nutritionFacts[0][1]
            fruit_container_2_value_.text = nutritionFacts[1][1]
            fruit_container_3_name_.text = nutritionFacts[0][2]
            fruit_container_3_value_.text = nutritionFacts[1][2]
            fruit_container_4_name_.text = nutritionFacts[0][3]
            fruit_container_4_value_.text = nutritionFacts[1][3]

            fruit_description_.text = this.productToShow.description

            val vitamins = Gson().fromJson(this.productToShow.contents, Array<Array<String>>::class.java)

            fruit_vitamin_1_name_.text = vitamins[0][0]
            fruit_vitamin_1_value_.text = vitamins[1][0]
            fruit_vitamin_2_name_.text = vitamins[0][1]
            fruit_vitamin_2_value_.text = vitamins[1][1]
            fruit_vitamin_3_name_.text = vitamins[0][2]
            fruit_vitamin_3_value_.text = vitamins[1][2]
            fruit_vitamin_4_name_.text = vitamins[0][3]
            fruit_vitamin_4_value_.text = vitamins[1][3]
            fruit_vitamin_5_name_.text = vitamins[0][4]
            fruit_vitamin_5_value_.text = vitamins[1][4]
            fruit_vitamin_6_name_.text = vitamins[0][5]
            fruit_vitamin_6_value_.text = vitamins[1][5]

            fruit_feedback_container_.visibility = View.GONE
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
            info_product_name_.text = productToShow.name

            if (!productToShow.image.isNullOrEmpty() || productToShow.image == "NULL") {
                Picasso.get().load(productToShow.image).error(R.drawable.ic_cereals__black)
                    .into(info_product_image_);
            } else {
                info_product_image_.setImageResource(R.drawable.ic_cereals__black)
            }

            // short version of the product
            if (productToShow.shrinked == true) {
                info_product_layout_.removeAllViews()
                info_product_layout_.visibility = View.GONE
            } else if (productToShow.shrinked == false) {
                if (productToShow.contents == "NULL" || productToShow.contents == "") {
                    info_contents_container_.visibility = View.GONE
                } else {
                    info_product_contents_.text = productToShow.contents
//                var collapsed = CollapseUtils(this, hide, info_product_contents)
//                collapsed.initDescription(productToShow.contents!!)
                }
                if (productToShow.manufacturer == "NULL" || productToShow.manufacturer == "") {
                    info_manufacturer_container_.visibility = View.GONE
                } else {
                    info_product_manufacturer_.text = productToShow.manufacturer
                }
                if (productToShow.description == "NULL" || productToShow.description == "") {
                    info_description_container_.visibility = View.GONE
                } else {
                    info_product_description_.text = productToShow.description
                }
                if (productToShow.categoryURL == "NULL" || productToShow.categoryURL == "") {
                    info_category_url_container_.visibility = View.GONE
                } else {
                    info_product_category_URL_.text = productToShow.categoryURL
                }
                if (productToShow.mass == "NULL" || productToShow.mass == "") {
                    info_mass_container_.visibility = View.GONE
                } else {
                    info_product_mass_.text = productToShow.mass
                }
                if (productToShow.bestBefore == "NULL" || productToShow.bestBefore == "") {
                    info_best_before_container_.visibility = View.GONE
                } else {
                    info_product_bestbefore_.text = productToShow.bestBefore
                }
                if (productToShow.nutrition == "NULL" || productToShow.nutrition == "") {
                    info_nutrition_facts_container_.visibility = View.GONE
                } else {
                    info_product_nutrition_facts_.text = productToShow.nutrition
                }
                if (!productToShow.ingredients.isNullOrEmpty()) {
                    if (productToShow != null) {
                        filter()
                        draw()
                    }
                } else {
                    info_ingredients_container_.visibility = View.GONE
                    info_feedback_container_.visibility = View.GONE
                }

                if (info_feedback_container_.visibility == View.VISIBLE) {
                    good_.setOnClickListener {
                        if (voted == false) {
                            Toast.makeText(
                                context!!, "Спасибо за отзыв!",
                                Toast.LENGTH_SHORT
                            ).show()
                            voted = true
                        } else {
                            Toast.makeText(
                                context!!, "Вы уже проголосовали!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    bad_.setOnClickListener {
                        if (voted == false) {
                            Toast.makeText(
                                context!!, "Спасибо за отзыв!",
                                Toast.LENGTH_SHORT
                            ).show()
                            voted = true
                        } else {
                            Toast.makeText(
                                context!!, "Вы уже проголосовали!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }

        //        !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

        return root
    }

    override fun onDismiss(dialogInterface: DialogInterface) {
        activity?.let {
            // Back to working state after the bottom sheet is dismissed.
            var wfm = ViewModelProviders.of(it).get<WorkflowModel>(
                WorkflowModel::class.java)
            wfm.setWorkflowState(WorkflowState.DETECTING)
        }
        super.onDismiss(dialogInterface)
    }

    companion object {

        private const val TAG = "BarcodeResultFragment"
        private const val ARG_BARCODE_FIELD_LIST = "arg_barcode_field_list"
        private const val ARG_PRODUCT = "product_entity"

        fun show(fragmentManager: FragmentManager, barcodeField: BarcodeField, p: ProductModel) {
            val barcodeResultFragment = BarcodeResultFragment()
            barcodeResultFragment.arguments = Bundle().apply {
                putSerializable(ARG_BARCODE_FIELD_LIST, barcodeField);
                putSerializable(ARG_PRODUCT, p)
            }
            barcodeResultFragment.show(fragmentManager, TAG)
        }

        fun dismiss(fragmentManager: FragmentManager) {
            (fragmentManager.findFragmentByTag(TAG) as BarcodeResultFragment?)?.dismiss()
        }
    }
}
