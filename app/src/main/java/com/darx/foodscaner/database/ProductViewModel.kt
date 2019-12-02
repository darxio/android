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
    private var favouriteProducts: LiveData<List<ProductModel>>? = null

    init {
        db = AppDatabase.getInstance(application.applicationContext)
        scannedProducts = db?.productsDAO()?.getScanned()
        favouriteProducts = db?.productsDAO()?.getFavourites()
    }

    fun getScanned_(): LiveData<List<ProductModel>> {
        return this.scannedProducts!!
    }

    fun getFavourites_(): LiveData<List<ProductModel>> {
        return this.favouriteProducts!!
    }

    fun getOne_(barcode: Long): LiveData<ProductModel>? {
        val product = db?.productsDAO()?.getOne(barcode)
        return product
    }

    fun add_(product: ProductModel) {
        service.submit { db?.productsDAO()?.add(product) }
    }

    fun upsert_(product: ProductModel) {
        service.submit { db?.productsDAO()?.upsert(product) }
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