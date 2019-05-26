package com.sk.bqgbook.app

import android.app.Application
import com.blankj.utilcode.util.Utils
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import com.silvericekey.skutilslibrary.NetUtils.HttpUtils
import com.sk.bqgbook.R
import io.realm.Realm
import io.realm.RealmConfiguration

class MyApplication : Application() {
    companion object {
        var instance: MyApplication? = null

        init {
            SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, layout ->
                layout.setPrimaryColorsId(R.color.white,R.color.colorPrimary)
                return@setDefaultRefreshHeaderCreator ClassicsHeader(context)
            }
            SmartRefreshLayout.setDefaultRefreshFooterCreator { context, layout ->
                return@setDefaultRefreshFooterCreator ClassicsFooter(context).setDrawableSize(20f)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        Utils.init(this)
        HttpUtils.setBaseUrl(CommonParams.base_url)
        initRealm()
    }

    private fun initRealm() {
        Realm.init(this)
        var config = RealmConfiguration.Builder().name("BQGBook.realm").deleteRealmIfMigrationNeeded().build()
        Realm.setDefaultConfiguration(config)
    }
}