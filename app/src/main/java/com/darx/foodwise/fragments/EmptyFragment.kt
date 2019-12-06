package com.darx.foodwise.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.darx.foodwise.R
import com.google.android.material.button.MaterialButton

class EmptyFragment(val backgroundResId: Int, val informationText: String, val buttonText: String, val orientation: Int, val buttonOnClickListner: Any) : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_empty, container, false)

        val picture = view.findViewById<ImageView>(R.id.empty_picture)
        val info = view.findViewById<TextView>(R.id.empty_info_text)
        val button = view.findViewById<MaterialButton>(R.id.permission_button)

        (view as LinearLayout).orientation = orientation

        picture.setImageResource(backgroundResId)

        info.text = informationText

        if (buttonText.isEmpty()) {
            button.visibility = View.INVISIBLE
        } else {
            button.text = buttonText
            button.setOnClickListener(buttonOnClickListner as View.OnClickListener?)
        }
        return view
    }
}