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

package com.darx.foodscaner.camerafragment.barcodedetection

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.darx.foodscaner.IngredientActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.darx.foodscaner.R
import com.darx.foodscaner.camerafragment.camera.WorkflowModel
import com.darx.foodscaner.camerafragment.camera.WorkflowModel.WorkflowState
import com.darx.foodscaner.database.*
import com.darx.foodscaner.models.IngredientExtended
import com.darx.foodscaner.services.ApiService
import com.darx.foodscaner.services.ConnectivityInterceptorImpl
import com.darx.foodscaner.services.NetworkDataSourceImpl
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.android.synthetic.main.barcode_bottom_sheet.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/** Displays the bottom sheet to present barcode fields contained in the detected barcode.  */
class BarcodeResultFragment : BottomSheetDialogFragment() {

// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

    private lateinit var productToShow: ProductModel
    private lateinit var pVM: ProductViewModel
    private lateinit var iVM: IngredientViewModel
    private lateinit var gVM: GroupViewModel
    private var networkDataSource: NetworkDataSourceImpl? = null
    var chips: ArrayList<Chip>? = ArrayList()
    private lateinit var layout: LinearLayout
    var ok: Boolean = true
    var cautious: Boolean = false

    val states = arrayOf(
        intArrayOf(android.R.attr.state_enabled), // enabled
        intArrayOf(-android.R.attr.state_enabled), // disabled
        intArrayOf(-android.R.attr.state_checked), // unchecked
        intArrayOf(android.R.attr.state_pressed)  // pressed
    )

    val positiveColors = intArrayOf(R.color.positiveColor, R.color.positiveColor, R.color.positiveColor, R.color.positiveColor)
    val negativeColors = intArrayOf(R.color.negativeColor, R.color.negativeColor, R.color.negativeColor, R.color.negativeColor)

    private fun preorder(ingredient: IngredientExtended?) {
        if (ingredient == null) {
            return
        }

        // logics
        val chip = Chip(context!!)
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
                ?.observe(this@BarcodeResultFragment, object : Observer<IngredientModel> {
                    override fun onChanged(t: IngredientModel?) {
                        if (t?.id == ingredient.id) {
                            chip.setChipBackgroundColorResource(R.drawable.bg_chip_state_list_negative)
                            this@BarcodeResultFragment.ok = false
                        } else {
                            if (ingredient.danger!! > 0) {
                                chip.setChipBackgroundColorResource(R.drawable.bg_chip_state_list_cautious)
                                this@BarcodeResultFragment.cautious = true
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

//        val title: TextView = view.findViewById(R.id.info_product_name)
//        title.text = barcodeField.label
//
//        val desc: TextView = view.findViewById(R.id.info_product_description)


//        !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

        val scale = resources.displayMetrics.density

        val apiService = ApiService(ConnectivityInterceptorImpl(context!!))
        networkDataSource = NetworkDataSourceImpl(apiService, context!!)
        networkDataSource?.ingredient?.observe(this, Observer {
            val intent = Intent(context!!, IngredientActivity::class.java)
            intent.putExtra("INGREDIENT", it)
            startActivity(intent)
        })

//        this.productToShow = intent?.extras?.get("PRODUCT") as ProductModel
        this.pVM = ViewModelProviders.of(this).get(ProductViewModel::class.java)
        this.iVM = ViewModelProviders.of(this).get(IngredientViewModel::class.java)
        this.gVM = ViewModelProviders.of(this).get(GroupViewModel::class.java)

        var starred = root.findViewById<ImageButton>(R.id.barcode_info_starred_ib)
        var share = root.findViewById<ImageButton>(R.id.barcode_info_share_btn)

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

//        back.setOnClickListener {
//            finish()
//        }

//        logics with info text views

        this.layout = root.findViewById(R.id.barcode_info_product_layout)

        val barcode_info_product_name_ = root.findViewById<View>(R.id.barcode_info_product_name) as TextView
        val barcode_info_product_ingredients_temp_text_view_ = root.findViewById<View>(R.id.barcode_info_product_ingredients_temp_text_view) as TextView
        val barcode_info_product_manufacturer_ = root.findViewById<View>(R.id.barcode_info_product_manufacturer) as TextView
        val barcode_info_product_description_ = root.findViewById<View>(R.id.barcode_info_product_description) as TextView
        val barcode_info_product_category_URL_ = root.findViewById<View>(R.id.barcode_info_product_category_URL) as TextView
        val barcode_info_product_mass_ = root.findViewById<View>(R.id.barcode_info_product_mass) as TextView
        val barcode_info_product_bestbefore_ = root.findViewById<View>(R.id.barcode_info_product_bestbefore) as TextView
        val barcode_info_product_nutrition_facts_ = root.findViewById<View>(R.id.barcode_info_product_nutrition_facts) as TextView


        barcode_info_product_name_.text = productToShow.name
        // when the short version of the product is obtained
        if (productToShow.contents == "") {
//            contents
            barcode_info_product_ingredients_temp_text_view_.text = "Информация недоступна."
            barcode_info_product_manufacturer_.text = "Информация недоступна."
            barcode_info_product_description_.text = "Информация недоступна."
            barcode_info_product_category_URL_.text = "Информация недоступна."
            barcode_info_product_mass_.text = "Информация недоступна."
            barcode_info_product_bestbefore_.text = "Информация недоступна."
            barcode_info_product_nutrition_facts_.text = "Информация недоступна."
        } else {
            if (productToShow.manufacturer != "NULL") {
                barcode_info_product_manufacturer_.text = productToShow.manufacturer
            } else {
                barcode_info_product_manufacturer_.text = "Информация недоступна."
            }
            if (productToShow.description != "NULL") {
                barcode_info_product_description_.text = productToShow.description
            } else {
                barcode_info_product_description_.text = "Информация недоступна."
            }
            if (productToShow.categoryURL != "NULL") {
                barcode_info_product_category_URL_.text = productToShow.categoryURL
            } else {
                barcode_info_product_category_URL_.text = "Информация недоступна."
            }
            if (productToShow.mass != "NULL") {
                barcode_info_product_mass_.text = productToShow.mass
            } else {
                barcode_info_product_mass_.text = "Информация недоступна."
            }
            if (productToShow.bestBefore != "NULL") {
                barcode_info_product_bestbefore_.text = productToShow.bestBefore
            } else {
                barcode_info_product_bestbefore_.text = "Информация недоступна."
            }
            if (productToShow.nutrition != "NULL") {
                barcode_info_product_nutrition_facts_.text = productToShow.nutrition
            } else {
                barcode_info_product_nutrition_facts_.text = "Информация недоступна."
            }
        }

        if (productToShow.ingredients.isNullOrEmpty()) {
            if (productToShow.contents != null || productToShow.contents != "" || productToShow.contents != "NULL") {
                val layout_contents = LinearLayout(context!!)
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

                val contentsLabelTextView = TextView(context!!)
                contentsLabelTextView.text = "Текст состава:"

                val contentsTextView = TextView(context!!)

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

        val barcode_info_ingredient_chips_ = root.findViewById<View>(R.id.barcode_info_ingredient_chips) as ChipGroup

        if (this.chips != null) {
            for (i in this.chips!!) {
                barcode_info_ingredient_chips_.addView(i)
            }
        }

        if (ok == false) {
            barcode_info_material_card.setBackgroundColor(R.color.negativeColor)
            barcode_info_product_warning_text.text = "Cодержит ингредиенты, которые вы не хотите есть!"
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
