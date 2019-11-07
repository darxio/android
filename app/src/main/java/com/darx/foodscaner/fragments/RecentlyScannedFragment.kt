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

//        val s = "{\"name\":\"Сливки натуральные\",\"danger\":-1,\"ingredients\":null}"
//        val ar = ArrayList<IngredientExtended>()
//            ar.add(IngredientExtended().fromString(s))
//
//        productViewModel?.add_(ProductModel(barcode = 222222222, name = "Chebupeli", description = "Ploxo",
//            ingredients = ar,
//            categoryURL = "/dsf",mass = "9324",bestBefore = "NULL",nutrition ="1",manufacturer = "43",image ="dfs"))

        productsAdapter =
                    ProductsAdapter(emptyList(),  productViewModel!!, this.context!!, object : ProductsAdapter.Callback {

                        override fun onItemClicked(item: ProductModel) {
                            val intent = Intent(this@RecentlyScannedFragment.activity, ProductActivity::class.java)
                            intent.putExtra("PRODUCT", item as Serializable)
                            startActivity(intent)
                        }

                    })

        view.recently_scanned_products_recycler_view.adapter = productsAdapter

        val tmp = productViewModel
        val tmpAdp = productsAdapter

        tmp!!.getAll_().observe(this@RecentlyScannedFragment, object : Observer<List<ProductModel>> {
            override fun onChanged(l: List<ProductModel>?) {
                tmpAdp!!.addItems(l ?: return)
            }
        })

        return view
    }
}
