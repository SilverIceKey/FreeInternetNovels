package com.sk.bqgbook.app

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.blankj.utilcode.util.Utils
import com.silvericekey.skutilslibrary.NetUtils.HttpUtils
import com.sk.bqgbook.R
import com.tencent.bugly.crashreport.CrashReport
import io.realm.Realm
import io.realm.RealmConfiguration

class MyApplication : MultiDexApplication() {
    companion object {
        var instance: MyApplication? = null
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(base)
    }
    override fun onCreate() {
        super.onCreate()
        instance = this
        Utils.init(this)
        HttpUtils.setBaseUrl(CommonParams.base_url)
        CrashReport.initCrashReport(getApplicationContext(), "306e37954d", false);
        initRealm()
    }

    private fun initRealm() {
        Realm.init(this)
        var config = RealmConfiguration.Builder().name("BQGBook.realm").deleteRealmIfMigrationNeeded().build()
        Realm.setDefaultConfiguration(config)
    }
}