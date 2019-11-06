package com.darx.foodscaner.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import java.util.concurrent.Executors

class IngredientViewModel(application: Application) : AndroidViewModel(application) {

    private var service = Executors.newFixedThreadPool(1)
    private var db: AppDatabase? = null

    private var excludedIngredients: LiveData<List<IngredientModel>>? = null
    private var exclIngredient: LiveData<IngredientModel>? = null

    init {
        db = AppDatabase.getInstance(application.applicationContext)
        excludedIngredients = db?.ingredientsDAO()?.getAll()
    }

    fun getAll_(): LiveData<List<IngredientModel>> {
        return this.excludedIngredients!!
    }

    // check if works
    fun getOne_(id: Int): LiveData<IngredientModel>? {
        service.submit {   exclIngredient = db?.ingredientsDAO()?.getOne(id) }
        return exclIngredient
    }

    fun add_(eI: IngredientModel) {
        service.submit { db?.ingredientsDAO()?.add(eI) }
    }

    fun deleteOne_(eI: IngredientModel) {
        service.submit { db?.ingredientsDAO()?.deleteOne(eI) }
    }

    fun deleteAll_() {
        service.submit { db?.ingredientsDAO()?.deleteAll() }
    }
}