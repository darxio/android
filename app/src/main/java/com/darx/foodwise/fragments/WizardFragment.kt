package com.darx.foodwise.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.darx.foodwise.*
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_wizard.*

class WizardFragment(private val textFirst: String, private val imageFirst: Int, private val textSecond: String, private val imageSecond: Int) : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_wizard, container, false)

        var wizardText1 = view.findViewById<TextView>(R.id.wizard_text_1)
        var wizardText2 = view.findViewById<TextView>(R.id.wizard_text_2)
        var wizardImage1 = view.findViewById<ImageView>(R.id.wizard_image_1)
        var wizardImage2 = view.findViewById<ImageView>(R.id.wizard_image_2)

        wizardText1.text = textFirst
        wizardImage1.setBackgroundResource(imageFirst)
        wizardText2.text = textSecond
        wizardImage2.setBackgroundResource(imageSecond)

        return view
    }
}
