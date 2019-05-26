package com.sk.bqgbook.app.net

import com.silvericekey.skutilslibrary.IOUtils.ThreadUtils
import com.silvericekey.skutilslibrary.NetUtils.HttpUtils
import com.silvericekey.skutilslibrary.NetUtils.NetCallback
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
        ThreadUtils.runOnIOThread {
            HttpUtils.getInstance()
                .execute(HttpUtils.getInstance().obtainClass(GetDocument::class.java).getDocument(url),
                    object : NetCallback<String> {
                        override fun onSuccess(response: String?) {
                            var document = Jsoup.parse(response)
                            if (document != null) {
                                ThreadUtils.runOnUiThread {
                                    if (netCallback != null) {
                                        println("netUtils" + netCallback)
                                        println("netUtils" + document.select("#chaptercontent").html())
                                        netCallback.onSuccess(document)
                                    }
                                }
                            }
                        }

                    })

        }
    }

    fun getMenu(url: String, netCallback: NetResponseCallback) {
        ThreadUtils.runOnIOThread {
            HttpUtils.getInstance()
                .execute(HttpUtils.getInstance().obtainClass(GetDocument::class.java).getMenu(url),
                    object : NetCallback<String> {
                        override fun onSuccess(response: String?) {
                            var document = Jsoup.parse(response)
                            if (document != null) {
                                ThreadUtils.runOnUiThread {
                                    if (netCallback != null) {
                                        println("netUtils" + netCallback)
                                        println("netUtils" + document.select("#chaptercontent").html())
                                        netCallback.onSuccess(document)
                                    }
                                }
                            }
                        }

                    })

        }
    }
}