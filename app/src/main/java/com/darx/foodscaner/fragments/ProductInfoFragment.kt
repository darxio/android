package com.darx.foodscaner.fragments


import androidx.fragment.app.Fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


import android.content.Intent
import androidx.core.graphics.drawable.toDrawable
import com.darx.foodscaner.*
import com.darx.foodscaner.models.Ingredient
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputLayout


class ProductInfoFragment() : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_product_info, container, false)

//
//            chip.setOnClickListener {
//                val intent = Intent(this@IngredientsFragment.activity, IngredientActivity::class.java)
////                intent.putExtra("PRODUCT", item as Serializable)
//                startActivity(intent)
//            }

        return view
    }

}
