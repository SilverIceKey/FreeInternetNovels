package com.sk.bqgbook.app

import android.app.Application
import com.silvericekey.skutilslibrary.SKUtilsLibrary
import com.silvericekey.skutilslibrary.utils.HttpUtil
import com.tencent.bugly.crashreport.CrashReport
import io.realm.Realm
import io.realm.RealmConfiguration

class MyApplication : Application() {
    companion object {
        var instance: MyApplication? = null
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        SKUtilsLibrary.init(this)
        HttpUtil.init(CommonParams.base_url)
        CrashReport.initCrashReport(getApplicationContext(), "306e37954d", false);
        initRealm()
    }

    private fun initRealm() {
        Realm.init(this)
        var config = RealmConfiguration.Builder().name("BQGBook.realm").deleteRealmIfMigrationNeeded().build()
        Realm.setDefaultConfiguration(config)
    }
}