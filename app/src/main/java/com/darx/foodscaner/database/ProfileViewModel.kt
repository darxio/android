package com.darx.foodscaner.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.Executors

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private var service = Executors.newFixedThreadPool(1)
    private var db: AppDatabase? = null

    private var profiles: LiveData<List<ProfileModel>>? = null
    private var profile: LiveData<ProfileModel>? = null

    init {
        db = AppDatabase.getInstance(application.applicationContext)
        profiles = db?.profileDAO()?.getAll()
    }

    fun getAll_(): LiveData<List<ProfileModel>> {
        return this.profiles!!
    }

    fun getCount_(): Int {
        return this.profiles?.value?.size ?: 0
    }

    fun add_(profile: ProfileModel) {
        service.submit { db?.profileDAO()?.add(profile) }
    }
}