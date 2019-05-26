package com.sk.bqgbook.app.net

import com.silvericekey.skutilslibrary.IOUtils.ThreadUtils
import org.jsoup.Jsoup

class NetUtils {
    companion object{
        fun getDocument(url:String,netCallback: NetCallback){
            ThreadUtils.runOnIOThread {
                try {
                    var document = Jsoup.connect(url).get()
                    if (document!=null){
                        ThreadUtils.runOnUiThread {
                            if (netCallback!=null){
                                netCallback.onSuccess(document)
                            }
                        }
                    }
                }catch (e:Exception){

                }
            }
        }
    }
}