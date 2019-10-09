package com.darx.foodscaner

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.darx.foodscaner.adapters.GroupAdapter
import com.darx.foodscaner.data.response.Group
import com.darx.foodscaner.services.ApiService
import kotlinx.android.synthetic.main.activity_groups.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class GroupsActivity : AppCompatActivity() {

    private val apiService = ApiService(/*ConnectivityInterceptorImpl(this.baseContext)*/)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_groups)

        val items = listOf(
            Group(1, "Вегетарианец", "Вкусно"),
            Group(2, "Веган", "Очень вкусно"),
            Group(3, "Мясоед", "Вкусно"),
            Group(4, "Солнцеед", "Очень вкусно"),
            Group(5, "Углеводная диета", "Вкусно")
        )
        val groupAdapter = GroupAdapter(items, object : GroupAdapter.Callback {
            override fun onItemClicked(item: Group) {
                val intent = Intent(this@GroupsActivity, GroupActivity::class.java)
                startActivity(intent)
            }
        })
        groupRecycler.adapter = groupAdapter
        apiService.groups()
    }

//    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
//
//        return super.onCreateView(name, context, attrs)
//    }

    private fun groups(view: View) {
        GlobalScope.launch(Dispatchers.Main) {
            val response = apiService.groups().await()
//            username.text = response.toString()


        }
    }

}
