package com.darx.foodscaner.database

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.Executors

class GroupViewModel(application: Application) : AndroidViewModel(application) {

    private var service = Executors.newFixedThreadPool(1)
    private var db: AppDatabase? = null

    private var groups: LiveData<List<GroupModel>>? = null
    private var group: LiveData<GroupModel>? = null

    init {
        db = AppDatabase.getInstance(application.applicationContext)
        groups = db?.groupsDAO()?.getAll()
    }

    fun getAll_(): LiveData<List<GroupModel>> {
        return this.groups!!
    }

    // check if works
    fun getOne_(id: Int): LiveData<GroupModel>? {
        group = db?.groupsDAO()?.getOne(id)
        return group
    }

    fun checkAll_(ids: ArrayList<Int>): LiveData<Boolean>? {
        return db?.groupsDAO()?.checkAll(ids.toList())
    }

//    fun find_(id: Int): Int {
//        var found: Int = 0
//        Observable.fromCallable{
//            db?.groupsDAO()?.find(id)
//        }.subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe{
//                found = it!!
//            }
//        return found
//    }

    fun add_(group: GroupModel) {
        service.submit { db?.groupsDAO()?.add(group) }
    }

    fun deleteOne_(group: GroupModel) {
        service.submit { db?.groupsDAO()?.deleteOne(group) }
    }

    fun deleteAll_() {
        service.submit { db?.groupsDAO()?.deleteAll() }
    }
}