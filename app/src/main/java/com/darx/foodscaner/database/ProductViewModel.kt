package com.darx.foodscaner.database

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.lang.Exception
import java.util.concurrent.Executors

class ProductViewModel(application: Application) : AndroidViewModel(application) {

    private var service = Executors.newFixedThreadPool(1)
    private var db: AppDatabase? = null

    private var scannedProducts: LiveData<List<ProductModel>>? = null
    private var scannedFavouriteProducts: LiveData<List<ProductModel>>? = null

    init {
        db = AppDatabase.getInstance(application.applicationContext)
        scannedProducts = db?.productsDAO()?.getAll()
        scannedFavouriteProducts = db?.productsDAO()?.getFavourites()
    }

    fun getAll_(): LiveData<List<ProductModel>> {
        return this.scannedProducts!!
    }

    fun getFavourites_(): LiveData<List<ProductModel>> {
        return this.scannedFavouriteProducts!!
    }

    fun add_(product: ProductModel) {
        service.submit {
            try {
                db?.productsDAO()?.add(product)
            } catch (e: Exception) {
                Log.e("PROBLEM", "err: ", e)
            }
        }
    }

    fun updateStarred_(product: ProductModel) {
        service.submit { db?.productsDAO()?.updateStarred(product) }
    }

    fun deleteOne_(product: ProductModel) {
        service.submit { db?.productsDAO()?.deleteOne(product) }
    }

    fun deleteAll_() {
        service.submit { db?.productsDAO()?.deleteAll() }
    }
}