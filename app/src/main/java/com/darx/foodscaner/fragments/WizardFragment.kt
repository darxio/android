package com.darx.foodscaner.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.darx.foodscaner.*

class WizardFragment(private val image: String, private val text: String) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_wizard, container, false)
        val wizardText = view.findViewById<TextView>(R.id.wizardText)
        wizardText.text = text
        return view
    }

}
