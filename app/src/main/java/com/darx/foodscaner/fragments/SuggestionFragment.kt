package com.darx.foodscaner.fragments


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatDialogFragment
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.darx.foodscaner.database.IngredientModel


class SuggestionFragment: AppCompatDialogFragment() {

    lateinit var ingr: IngredientModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) : View?  {
        super.onCreate(savedInstanceState)

        val arguments = arguments
        this.ingr = if (arguments?.containsKey(ARG_INGREDIENT) == true) {
            arguments.getSerializable(ARG_INGREDIENT) as IngredientModel? ?: IngredientModel()
        } else {
            Log.e(TAG, "No ingredient passed in!")
            IngredientModel()
        }

        return view
    }

    companion object {

        private const val TAG = "SuggestionFragment"
        private const val ARG_INGREDIENT = "arg_ingredient"

        fun show(fragmentManager: FragmentManager, ingredient: IngredientModel) {
            val suggestionFragment = SuggestionFragment()
            suggestionFragment.arguments = Bundle().apply {
                putSerializable(ARG_INGREDIENT, ingredient)
            }
            suggestionFragment.show(fragmentManager, TAG)
        }

        fun dismiss(fragmentManager: FragmentManager) {
            (fragmentManager.findFragmentByTag(TAG) as SuggestionFragment?)?.dismiss()
        }
    }
}
