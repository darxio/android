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
import android.content.Intent
import android.content.res.Resources
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
import com.darx.foodwise.IngredientActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.darx.foodwise.R
import com.darx.foodwise.camerafragment.camera.WorkflowModel
import com.darx.foodwise.camerafragment.camera.WorkflowModel.WorkflowState
import com.darx.foodwise.database.*
import com.darx.foodwise.database.IngredientExtended
import com.darx.foodwise.services.ApiService
import com.darx.foodwise.services.ConnectivityInterceptorImpl
import com.darx.foodwise.services.NetworkDataSourceImpl
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_product.*
import kotlinx.android.synthetic.main.barcode_bottom_sheet.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

/** Displays the bottom sheet to present barcode fields contained in the detected barcode.  */
class BarcodeResultFragment : BottomSheetDialogFragment(), TextToSpeech.OnInitListener {

// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
private lateinit var productToShow: ProductModel
    private lateinit var pVM: ProductViewModel
    private lateinit var iVM: IngredientViewModel
    private lateinit var gVM: GroupViewModel
    private var networkDataSource: NetworkDataSourceImpl? = null
    var chips: ArrayList<Chip>? = ArrayList()
    var voted: Boolean = false

    private var speaker: ImageButton? = null
    private var tts: TextToSpeech? = null

    private var isAllowed: Boolean = true

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

    private fun preorder(ingredient: IngredientExtended?, showDanger: Boolean) {
        if (ingredient == null) {
            return
        }

        val chip = Chip(context!!)
        if (ingredient.name != "") {
            chip.text = ingredient.name
        }

        var isGroupsMatched = false

        if (ingredient.groups.isNullOrEmpty()) {
            ingredient.groups = ArrayList()
        }
        gVM.checkAll_(ingredient.groups)?.observe(this, object : Observer<Boolean> {
            override fun onChanged(t: Boolean?) {
                isGroupsMatched = t ?: false
                if (checkStatus(null, isGroupsMatched)) {
                    chip.setChipBackgroundColorResource(R.drawable.bg_chip_state_list_positive)
                } else {
                    chip.setChipBackgroundColorResource(R.drawable.bg_chip_state_list_negative)
                }
            }
        })

        iVM.getOne_(ingredient.id)?.observe(this, object : Observer<IngredientModel> {
            override fun onChanged(t: IngredientModel?) {
                if (checkStatus(t, isGroupsMatched)) {
                    chip.setChipBackgroundColorResource(R.drawable.bg_chip_state_list_positive)
                    if (ingredient.danger!! > 1 && showDanger) {
                        chip.setChipBackgroundColorResource(R.drawable.bg_chip_state_list_cautious)
                    }
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
                preorder(i, showDanger)
            }
        }

        chip.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                networkDataSource!!.getIngredientByID(ingredient.id)
            }
        }
    }

// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

    var barcodeField: BarcodeField? = null

    override fun onCreateView(
        layoutInflater: LayoutInflater,
        viewGroup: ViewGroup?,
        bundle: Bundle?
    ): View {
        val root: View = layoutInflater.inflate(R.layout.barcode_bottom_sheet, viewGroup, false)

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
        val product_toolbar_ = root.findViewById<androidx.appcompat.widget.Toolbar>(R.id.product_toolbar)
        product_toolbar_.visibility = View.GONE
        val info_product_name_ = root.findViewById<TextView>(R.id.info_product_name)
        val info_product_image_ = root.findViewById<ImageView>(R.id.info_product_image)
        val info_product_layout_ = root.findViewById<LinearLayout>(R.id.info_product_layout)
        val info_product_contents_ = root.findViewById<TextView>(R.id.info_product_contents)
        val info_contents_container_ = root.findViewById<LinearLayout>(R.id.info_contents_container)
        val info_product_manufacturer_ = root.findViewById<TextView>(R.id.info_product_manufacturer)
        val info_manufacturer_container_ = root.findViewById<LinearLayout>(R.id.info_manufacturer_container)
        val info_product_description_ = root.findViewById<TextView>(R.id.info_product_description)
        val info_description_container_ = root.findViewById<LinearLayout>(R.id.info_description_container)
        val info_product_mass_ = root.findViewById<TextView>(R.id.info_product_mass)
        val info_mass_container_ = root.findViewById<LinearLayout>(R.id.info_mass_container)
        val info_product_category_URL_ = root.findViewById<TextView>(R.id.info_product_category_URL)
        val info_category_url_container_ = root.findViewById<LinearLayout>(R.id.info_category_url_container)
        val info_product_bestbefore_ = root.findViewById<TextView>(R.id.info_product_bestbefore)
        val info_best_before_container_ = root.findViewById<LinearLayout>(R.id.info_best_before_container)
        val info_product_nutrition_facts_ = root.findViewById<TextView>(R.id.info_product_nutrition_facts)
        val info_nutrition_facts_container_ = root.findViewById<LinearLayout>(R.id.info_nutrition_facts_container)
        val info_ingredient_chips_ = root.findViewById<ChipGroup>(R.id.info_ingredient_chips)
        val info_ingredients_container_ = root.findViewById<LinearLayout>(R.id.info_ingredients_container)
        val info_feedback_container_ = root.findViewById<LinearLayout>(R.id.info_feedback_container)
        val good_ = root.findViewById<ImageButton>(R.id.good)
        val bad_ = root.findViewById<ImageButton>(R.id.bad)

        var spoke = false
        speaker = root.findViewById(R.id.info_speaker_ib)
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
        info_product_name_.text = productToShow.name

        if (!productToShow.image.isNullOrEmpty() || productToShow.image == "NULL") {
            Picasso.get().load(productToShow.image).error(R.drawable.ic_cereals__black).into(info_product_image_);
        } else {
            info_product_image_.setImageResource(R.drawable.ic_cereals__black)
        }

        // short version of the product
        if (productToShow.shrinked == true) {
            info_product_layout_.removeAllViews()

            // to do -- empty fragment here

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
                for (i in productToShow.ingredients!!) {
                    if (i.name.isNotEmpty()) {
                        preorder(i, true)
                    }
                }

                if (this.chips != null) {
                    for (i in this.chips!!) {
                        info_ingredient_chips_.addView(i)
                    }
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
