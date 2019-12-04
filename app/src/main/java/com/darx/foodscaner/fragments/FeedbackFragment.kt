package com.darx.foodscaner.fragments


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatDialogFragment
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.darx.foodscaner.R
import com.darx.foodscaner.database.IngredientModel
import com.darx.foodscaner.services.ApiService
import com.darx.foodscaner.services.ConnectivityInterceptorImpl
import com.darx.foodscaner.services.NetworkDataSourceImpl


class FeedbackFragment: AppCompatDialogFragment() {

    private var networkDataSource: NetworkDataSourceImpl? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) : View?  {
        super.onCreate(savedInstanceState)

        val arguments = arguments

        var barcode: Long =
            if (arguments?.containsKey(ARG_BARCODE) == true) {
                arguments.getSerializable(ARG_BARCODE) as Long? ?: 0
            } else {
                Log.e(TAG, "No barcode value passed in!")
                0
            }

        val view = inflater.inflate(R.layout.fragment_add_product, container, false)
        val skip = view.findViewById<Button>(R.id.skip_btn)
        val add = view.findViewById<Button>(R.id.add_btn)
        val name = view.findViewById<EditText>(R.id.name_et)

        val apiService = ApiService(ConnectivityInterceptorImpl(this.context!!))
        networkDataSource = NetworkDataSourceImpl(apiService, context!!)

        skip.setOnClickListener{
            this.dismiss()
        }

        add.setOnClickListener{
            val nameStr = name.text.toString().trim()
            if (nameStr != "" && nameStr.length > 0) {
                // запрос в сеть
                //nameStr & barcode
//                apiService.productAdd(barcode, nameStr)
                Toast.makeText(context!!, "Спасибо!", Toast.LENGTH_SHORT).show()
                this.dismiss()
            } else {
                Toast.makeText(context!!, "Введите название продукта", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    companion object {

        private const val TAG = "AddProductFragment"
        private const val ARG_BARCODE = "arg_barcode"

        fun show(fragmentManager: FragmentManager, barcode: Long) {
            val addProductFragment = FeedbackFragment()
            addProductFragment.arguments = Bundle().apply {
                putSerializable(ARG_BARCODE, barcode)
            }
            addProductFragment.show(fragmentManager, TAG)
        }

        fun dismiss(fragmentManager: FragmentManager) {
            (fragmentManager.findFragmentByTag(TAG) as FeedbackFragment?)?.dismiss()
        }
    }
}
