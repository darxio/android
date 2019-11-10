package com.darx.foodscaner.fragments


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.darx.foodscaner.ProductActivity
import com.darx.foodscaner.adapters.ProductsAdapter
import com.darx.foodscaner.database.ProductModel
import com.darx.foodscaner.database.ProductViewModel
import com.darx.foodscaner.models.IngredientExtended
import com.darx.foodscaner.utils.SerializableJSONArray
import com.darx.foodscaner.utils.SerializableJSONObject
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_recently_scanned.view.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.Serializable


class RecentlyScannedFragment : Fragment() {

    private var productViewModel: ProductViewModel? = null
    private var productsAdapter: ProductsAdapter? = null

    @SuppressLint("CheckResult")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(com.darx.foodscaner.R.layout.fragment_recently_scanned, container, false)

        this.productViewModel = ViewModelProviders.of(this).get(ProductViewModel::class.java)

//        productViewModel?.add_(ProductModel(barcode = 4607111033451, name = "ГОТОВЫЕ ЧЕБУПЕЛИ ВЕТЧИНА+СЫР ТМ ГОРЯЧАЯ ШТУЧКА", description = "Изделия кулинарные мясосодержащие. Готовые вторые блюда замороженные.", contents = "",
//            ingredients = null,
//            categoryURL = "Товары/Продукты питания/Замороженные продукты/Полуфабрикаты замороженные/Чебуреки замороженные",
//            mass = "300,00 г",bestBefore = "6 мес.",nutrition ="Белки: 8,00 г",manufacturer = "NULL",image ="http://www.goodsmatrix.ru/BigImages/4607111033451.jpg"))

        productsAdapter =
                    ProductsAdapter(emptyList(),  productViewModel!!, this.context!!, object : ProductsAdapter.Callback {

                        override fun onItemClicked(item: ProductModel) {
                            val intent = Intent(this@RecentlyScannedFragment.activity, ProductActivity::class.java)
                            intent.putExtra("PRODUCT", item as Serializable)
                            startActivity(intent)
                        }

                    })

        view.recently_scanned_products_recycler_view.adapter = productsAdapter

        productViewModel!!.getAll_().observe(this@RecentlyScannedFragment, object : Observer<List<ProductModel>> {
            override fun onChanged(l: List<ProductModel>?) {
                productsAdapter!!.addItems(l ?: return)
            }
        })

        return view
    }
}
