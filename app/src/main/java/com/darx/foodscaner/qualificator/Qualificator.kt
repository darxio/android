package com.darx.foodscaner.qualificator

//import androidx.lifecycle.ViewModelProviders
//import com.darx.foodscaner.database.ExcludedIngredientViewModel
//import org.json.JSONArray
//
//class Qualificator {
//    private var excludedIngredientViewModel: ExcludedIngredientViewModel? = null
//
//    public fun qualifyIngredients(ingredients: JSONArray): Array<Boolean> {
//        excludedIngredientViewModel = ViewModelProviders.of().get(ExcludedIngredientViewModel::class.java)
//
//        var tmpVM = excludedIngredientViewModel
//        var excludedIngredients = tmpVM!!.getAll_().value
//        var isOK = Array<Boolean>(excludedIngredients.size)
//        // for ()
//
//        return isOK
//    }
//
//
//    public fun qualifyGroupIngredients(ingredients: JSONArray): Array<Boolean> {
//        var excludedIngredients = excludedIngredientViewModel.getAll_()
//        var isOK = Array<Boolean>(excludedIngredients.size)
//        // for ()
//
//        return isOK
//    }
//}