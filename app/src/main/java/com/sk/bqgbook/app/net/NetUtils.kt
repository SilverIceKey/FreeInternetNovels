package com.sk.bqgbook.app.net

import com.silvericekey.skutilslibrary.rxjava.execute
import com.silvericekey.skutilslibrary.utils.HttpUtil
import com.sk.bqgbook.mvc.net.GetDocument
import org.jsoup.Jsoup

class NetUtils {
    companion object {
        private var instance: NetUtils? = null
        fun getInstance(): NetUtils? {
            if (instance == null) {
                synchronized(NetUtils::class.java) {
                    if (instance == null) {
                        instance = NetUtils()
                    }
                }
            }
            return instance
        }
    }

    fun getDocument(url: String, netCallback: NetResponseCallback) {
        HttpUtil.getInstance().obtainClass(GetDocument::class.java).getDocument(url).execute({
            var document = Jsoup.parse(it)
            if (document != null) {
                if (netCallback != null) {
                    println("netUtils" + netCallback)
                    println("netUtils" + document.select("#chaptercontent").html())
                    netCallback.onSuccess(document)
                }
            }
        }, {})
    }

    fun getMenu(url: String, netCallback: NetResponseCallback) {
        HttpUtil.getInstance().obtainClass(GetDocument::class.java).getMenu(url).execute({
            var document = Jsoup.parse(it)
            if (document != null) {
                if (netCallback != null) {
                    println("netUtils" + netCallback)
                    println("netUtils" + document.select("#chaptercontent").html())
                    netCallback.onSuccess(document)
                }
            }
        }, {

        })
    }
}