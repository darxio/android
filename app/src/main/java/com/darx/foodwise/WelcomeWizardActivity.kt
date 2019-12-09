package com.darx.foodwise

import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import com.darx.foodwise.adapters.PageAdapter
import com.darx.foodwise.fragments.WizardFragment
import kotlinx.android.synthetic.main.activity_welcome_wizard.*

class WelcomeWizardActivity : AppCompatActivity() {

    private val pagerAdapter = PageAdapter(supportFragmentManager, lifecycle)
    private var dots: Array<TextView?> ?= null

    private var currentPage = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome_wizard)

        val prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("firstLaunch", false).apply()

        pagerAdapter.addFragment(WizardFragment("Нажимайте на ингредиенты, чтобы исключать их из рациона",R.drawable.first,"Обращайте внимание на уровни опасности ингредиентов и читайте информацию о них",R.drawable.ic_warning_first), "Wizard1")
        pagerAdapter.addFragment(WizardFragment("Вступайте в группы, чтобы сразу исключать целый набор ингредиентов", R.drawable.third,"Изучайте списки ингредиентов каждой из 6 групп",R.drawable.fourth), "Wizard2")
        pagerAdapter.addFragment(WizardFragment("Сканируйте штрих-коды продуктов, чтобы понять, подходят они вам или нет", R.drawable.fifth,"Переходите в режим распознавания фруктов и овощей, чтобы узнать о их пищевой ценности", R.drawable.sixth), "Wizard3")

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

        wizardPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                currentPage = position
                if (currentPage == 0) {
                    buttonNext.isEnabled = true
                    buttonPrev.isEnabled = false
                    buttonPrev.visibility = View.INVISIBLE

                    buttonNext.text = "ВПЕРЁД"
                    buttonPrev.text = ""
                } else if (position == dots?.size!! - 1) {
                    buttonNext.isEnabled = true
                    buttonPrev.isEnabled = true
                    buttonPrev.visibility = View.VISIBLE

                    buttonNext.text = "ЗАКОНЧИТЬ"
                    buttonPrev.text = "НАЗАД"
                } else {
                    buttonNext.isEnabled = true
                    buttonPrev.isEnabled = true
                    buttonPrev.visibility = View.VISIBLE

                    buttonNext.text = "ВПЕРЁД"
                    buttonPrev.text = "НАЗАД"
                }

                addDotsIndicator(pagerAdapter.itemCount, position)
            }
        })
    }

    private fun addDotsIndicator(count: Int, current: Int) {
        dots = arrayOfNulls(count)
        wizardLayout.removeAllViews()

        for (i in 0 until dots?.size!!) {
            dots!![i] = TextView(this)
            dots!![i]?.text = Html.fromHtml("&#8226")
            dots!![i]?.textSize = 35F
            if (i == current) {
                dots!![i]?.setTextColor(Color.WHITE)
            } else {
                dots!![i]?.setTextColor(Color.GRAY)
            }
            wizardLayout.addView(dots!![i])
        }
    }
}
