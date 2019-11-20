package com.darx.foodscaner.fragments


import android.content.ContentValues.TAG
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
import com.darx.foodscaner.camerafragment.camera.WorkflowModel
import com.darx.foodscaner.database.IngredientModel
import com.darx.foodscaner.services.ApiService
import com.darx.foodscaner.services.ConnectivityInterceptorImpl
import com.darx.foodscaner.services.NetworkDataSource
import com.darx.foodscaner.services.NetworkDataSourceImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class AddProductFragment: AppCompatDialogFragment() {

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
                GlobalScope.launch(Dispatchers.Main) {
                    networkDataSource?.productAdd(
                        barcode,
                        nameStr,
                        object : NetworkDataSource.Callback {
                            override fun onTimeoutException() {
                                Log.e("HTTP", "Wrong answer.")
                                Toast.makeText(
                                    context!!, "Проблемы с интернетом!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            override fun onException() {
                                Log.e("HTTP", "EXCEPTION CAUGHT.")
                                Toast.makeText(
                                    context!!, "Неизвестная ошибка",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            override fun onNoConnectivityException() {
                                Log.e("HTTP", "Wrong answer.")
                                Toast.makeText(
                                    context!!, "Проблемы с интернетом!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                NetworkDataSource.DefaultCallback(context!!)
                                    .onNoConnectivityException()
                            }

                            override fun onHttpException() {
                                Log.e("HTTP", "Wrong answer.")
                                Toast.makeText(
                                    context!!, "Ошибка соединения",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        })
                }
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
        private const val ARG_BARCODE = "ARG_BARCODE"

        fun show(fragmentManager: FragmentManager, barcode: Long) {
            val addProductFragment = AddProductFragment()
            addProductFragment.arguments = Bundle().apply {
                putSerializable(ARG_BARCODE, barcode)
            }
            addProductFragment.show(fragmentManager, TAG)
        }

        fun dismiss(fragmentManager: FragmentManager) {
            (fragmentManager.findFragmentByTag(TAG) as AddProductFragment?)?.dismiss()
        }
    }
}
