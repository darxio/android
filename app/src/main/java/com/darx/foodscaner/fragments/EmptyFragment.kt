package com.darx.foodscaner.fragments

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.darx.foodscaner.R
import com.google.android.material.button.MaterialButton

class EmptyFragment(val backgroundResId: Int, val informationText: String, val buttonText: String, val buttonOnClickListner: View.OnClickListener) : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_empty, container, false)

        var picture = view.findViewById<ImageView>(R.id.empty_picture)
        var info = view.findViewById<TextView>(R.id.empty_info_text)
        var button = view.findViewById<MaterialButton>(R.id.empty_button)

        picture.setBackgroundResource(backgroundResId)

        info.setText(informationText)

        button.setText(buttonText)
        button.setOnClickListener(buttonOnClickListner)

        return view
    }
}