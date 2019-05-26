package com.sk.bqgbook.mvc.ui.interfaces

import android.app.Activity
import android.graphics.Color
import android.webkit.JavascriptInterface
import com.blankj.utilcode.util.BarUtils
import com.silvericekey.skutilslibrary.IOUtils.ThreadUtils

class WebViewInterface {
    var activity: Activity
    var bookTitleCallback: MainBookTitleCallback

    constructor(activity: Activity,bookTitleCallback: MainBookTitleCallback) {
        this.activity = activity
        this.bookTitleCallback = bookTitleCallback
    }

    @JavascriptInterface
    open fun setBarColor(color: String) {
        if (color.contains("rgb")) {
            var hexColor = ""
            hexColor = color.replace("rgb", "").replace(" ", "")
            hexColor = hexColor.replace("(", "")
            hexColor = hexColor.replace(")", "")
            ThreadUtils.runOnUiThread {
                BarUtils.setStatusBarColor(
                    activity,
                    Color.parseColor(
                        "#" + hexColor.split(",")[0].toInt().toString(16) + hexColor.split(",")[1].toInt().toString(
                            16
                        ) + hexColor.split(",")[2].toInt().toString(16)
                    )
                )
            }
        }
    }

    @JavascriptInterface
    fun setBookTitle(bookTitle:String){
        if (bookTitleCallback!=null){
            bookTitleCallback.setBookTitle(bookTitle)
        }
    }
}