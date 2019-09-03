package com.sk.bqgbook.mvc.ui.interfaces

import android.app.Activity
import android.graphics.Color
import android.webkit.JavascriptInterface
import com.blankj.utilcode.util.BarUtils
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class WebViewInterface {
    var activity: Activity
    var bookTitleCallback: MainBookTitleCallback

    constructor(activity: Activity, bookTitleCallback: MainBookTitleCallback) {
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
            Observable.create(object : ObservableOnSubscribe<Int> {
                override fun subscribe(emitter: ObservableEmitter<Int>) {
                    emitter.onNext(Color.parseColor(
                            "#" + hexColor.split(",")[0].toInt().toString(16) + hexColor.split(",")[1].toInt().toString(
                                    16
                            ) + hexColor.split(",")[2].toInt().toString(16)
                    ))
                }
            })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        BarUtils.setStatusBarColor(activity, it)
                    })

        }
    }

    @JavascriptInterface
    fun setBookTitle(bookTitle: String) {
        if (bookTitleCallback != null) {
            bookTitleCallback.setBookTitle(bookTitle)
        }
    }
}