package com.darx.foodscaner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import com.darx.foodscaner.adapters.PageAdapter
import com.darx.foodscaner.fragments.WizardFragment
import kotlinx.android.synthetic.main.activity_welcome_wizard.*

class WelcomeWizardActivity : AppCompatActivity() {

    private val pagerAdapter = PageAdapter(supportFragmentManager, lifecycle)
    private var dots: Array<TextView?> ?= null

    private var currentPage = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome_wizard)

        pagerAdapter.addFragment(WizardFragment("", "Выберете группы к которым вы относитесь и ингредиенты которые не едите: ..."), "Wizard1")
        pagerAdapter.addFragment(WizardFragment("", "Отсканируйте продукт, чтобы понять подходит он вам или нет" ), "Wizard2")
        pagerAdapter.addFragment(WizardFragment("", "Посмотрите информацию о продукте"), "Wizard3")

        wizardPager.adapter = pagerAdapter

        buttonNext.setOnClickListener {
            if ((currentPage + 1) == pagerAdapter.itemCount) {
                this.finish()
            }
            wizardPager.setCurrentItem(currentPage + 1)
        }

        buttonPrev.setOnClickListener {
            wizardPager.setCurrentItem(currentPage - 1)
        }

//        addDotsIndicator(pagerAdapter.itemCount, currentPage)

        wizardPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                currentPage = position
                if (currentPage == 0) {
                    buttonNext.isEnabled = true
                    buttonPrev.isEnabled = false
                    buttonPrev.visibility = View.INVISIBLE

                    buttonNext.text = "Next"
                    buttonPrev.text = ""
                } else if (position == dots?.size!! - 1) {
                    buttonNext.isEnabled = true
                    buttonPrev.isEnabled = true
                    buttonPrev.visibility = View.VISIBLE

                    buttonNext.text = "Finish"
                    buttonPrev.text = "Back"
                } else {
                    buttonNext.isEnabled = true
                    buttonPrev.isEnabled = true
                    buttonPrev.visibility = View.VISIBLE

                    buttonNext.text = "Next"
                    buttonPrev.text = "Back"
                }

                addDotsIndicator(pagerAdapter.itemCount, position)
            }
        })
    }

    private fun addDotsIndicator(count: Int, current: Int) {
        dots = arrayOfNulls<TextView>(count)
        wizardLayout.removeAllViews()

        for (i in 0 until dots?.size!!) {
            dots!![i] = TextView(this)
            dots!![i]?.text = Html.fromHtml("&#8226")
            dots!![i]?.textSize = 35F
            if (i == current) {
                dots!![i]?.setTextColor(0xff00ff00.toInt())
            } else {
                dots!![i]?.setTextColor(0xff0000ff.toInt())
            }
            wizardLayout.addView(dots!![i])
        }
    }
}
