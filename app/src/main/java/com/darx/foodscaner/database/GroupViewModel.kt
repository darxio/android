package com.darx.foodscaner.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import java.util.concurrent.Executors

class GroupViewModel(application: Application) : AndroidViewModel(application) {

    private var service = Executors.newFixedThreadPool(1)
    private var db: AppDatabase? = null

    private var groups: LiveData<List<GroupModel>>? = null
    private var group: GroupModel? = null

    init {
        db = AppDatabase.getInstance(application.applicationContext)
        groups = db?.groupsDAO()?.getAll()
    }

    fun getAll_(): LiveData<List<GroupModel>> {
        return this.groups!!
    }

    // check if works
    fun getOne_(id: Int): GroupModel {
        service.submit {   group = db?.groupsDAO()?.getOne(id) }
        return this.group!!
    }

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