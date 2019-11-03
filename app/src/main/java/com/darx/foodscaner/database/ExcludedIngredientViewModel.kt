package com.darx.foodscaner.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import java.util.concurrent.Executors

class ExcludedIngredientViewModel(application: Application) : AndroidViewModel(application) {

    private var service = Executors.newFixedThreadPool(1)
    private var db: AppDatabase? = null

    private var excludedIngredients: LiveData<List<ExcludedIngredientModel>>? = null
    private var exclIngredient: ExcludedIngredientModel? = null

    init {
        db = AppDatabase.getInstance(application.applicationContext)
        excludedIngredients = db?.excludedIngredientsDAO()?.getAll()
    }

    fun getAll_(): LiveData<List<ExcludedIngredientModel>> {
        return this.excludedIngredients!!
    }

    // check if works
    fun getOne_(id: Int): ExcludedIngredientModel {
        service.submit {   exclIngredient = db?.excludedIngredientsDAO()?.getOne(id) }
        return this.exclIngredient!!
    }

    fun add_(eI: ExcludedIngredientModel) {
        service.submit { db?.excludedIngredientsDAO()?.add(eI) }
    }

    fun deleteOne_(eI: ExcludedIngredientModel) {
        service.submit { db?.excludedIngredientsDAO()?.deleteOne(eI) }
    }

    fun deleteAll_() {
        service.submit { db?.excludedIngredientsDAO()?.deleteAll() }
    }
}