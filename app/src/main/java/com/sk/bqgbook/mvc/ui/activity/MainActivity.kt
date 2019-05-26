package com.sk.bqgbook.mvc.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.StringUtils
import com.just.agentweb.AgentWeb
import com.just.agentweb.IAgentWebSettings
import com.sk.bqgbook.R
import com.sk.bqgbook.app.CommonParams
import com.sk.bqgbook.app.net.NetCallback
import com.sk.bqgbook.app.net.NetUtils
import com.sk.bqgbook.mvc.model.BookMenu
import com.sk.bqgbook.mvc.model.Books
import com.sk.bqgbook.mvc.ui.interfaces.MainBookTitleCallback
import com.sk.bqgbook.mvc.ui.interfaces.WebViewInterface
import com.vicpin.krealmextensions.query
import com.vicpin.krealmextensions.queryAll
import com.vicpin.krealmextensions.save
import kotlinx.android.synthetic.main.activity_main.*
import org.jsoup.nodes.Document

class MainActivity : AppCompatActivity(), MainBookTitleCallback {

    lateinit var agentWeb: AgentWeb
    var mBookTitle = ""
    var mBookCode = ""
    var webChromeClient: WebChromeClient = object : WebChromeClient() {

    }

    var webViewClient: WebViewClient = object : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            agentWeb.webCreator.webView.settings.blockNetworkImage = true
            if (request!!.url.toString().endsWith("all.html")) {
                var book = Books().query { equalTo("book",mBookTitle) }
                if (book.size!=0){
                    if (book[0].bookLook!=0){
                        var intent = Intent(this@MainActivity, BookContentActivity::class.java)
                        intent.putExtra("title", mBookTitle)
                        intent.putExtra("position", book[0].bookLook)
                        intent.putExtra("bookcode", mBookCode)
                        startActivity(intent)
                    }else{
                        val intent = Intent(this@MainActivity, BookMenuActivity::class.java)
                        intent.putExtra("url", request.url.toString())
                        intent.putExtra("title", mBookTitle)
                        intent.putExtra("bookcode", mBookCode)
                        startActivity(intent)
                    }
                    return true
                }else{
                    val intent = Intent(this@MainActivity, BookMenuActivity::class.java)
                    intent.putExtra("url", request.url.toString())
                    intent.putExtra("title", mBookTitle)
                    intent.putExtra("bookcode", mBookCode)
                    startActivity(intent)
                }
                return true
            }
            if (request.url.toString().contains(CommonParams.base_url)&&!StringUtils.isEmpty(mBookCode)&&request.url.toString().contains(mBookCode)&&request.url.toString().contains(".html")) {
                var book = Books().query { equalTo("book",mBookTitle) }
                if (book.size!=0){
                    var menus = book[0].bookMenu
                    for ((index,menuItem) in menus!!.withIndex()){
                        if (request.url.toString().replace(CommonParams.base_url,"/").equals(menuItem.link)){
                            book[0].bookLook = index
                            var intent = Intent(this@MainActivity, BookContentActivity::class.java)
                            intent.putExtra("title", mBookTitle)
                            intent.putExtra("position", book[0].bookLook)
                            intent.putExtra("bookcode", mBookCode)
                            startActivity(intent)
                        }
                    }
                    return true
                }else{
                    updateMenu(request)
                }
                return true
            }
            return super.shouldOverrideUrlLoading(view, request)
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            agentWeb.webCreator.webView.settings.blockNetworkImage = false
            if (!url!!.endsWith(CommonParams.base_url)&&!url.endsWith("html")){
                mBookCode = url!!.replace(CommonParams.base_url,"").replace("/","")
            }
            view!!.loadUrl("javascript:window.android.setBarColor($('header').css('background-color'));")
            view.loadUrl("javascript:window.android.setBookTitle($('span.title').html());")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        web_content.setPadding(0, BarUtils.getStatusBarHeight(), 0, 0)
        agentWeb = AgentWeb.with(this)
            .setAgentWebParent(web_content, LinearLayout.LayoutParams(-1, -1))
            .useDefaultIndicator()
            .setWebChromeClient(webChromeClient)
            .setWebViewClient(webViewClient)
            .createAgentWeb().ready().go(CommonParams.base_url)
        agentWeb.webCreator.webView.settings.blockNetworkImage = true
        agentWeb.jsInterfaceHolder.addJavaObject("android", WebViewInterface(this,this))
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_BACK -> {
                if (agentWeb.webCreator.webView.canGoBack()) {
                    agentWeb.webCreator.webView.goBack()
                    return true
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun setBookTitle(bookTitle: String) {
        mBookTitle = bookTitle
    }

    fun updateMenu(request: WebResourceRequest?) {
        NetUtils.getDocument(CommonParams.base_url + mBookCode + "/all.html", object : NetCallback {
            override fun onSuccess(document: Document) {
                var books = Books().query { equalTo("book",mBookTitle) }
                lateinit var book: Books
                if (books.size == 0) {
                    book = Books()
                    book.id = Books().queryAll().size.toLong()
                    book.bookCode = mBookCode
                } else {
                    book = books.get(0)
                }
                book.book = mBookTitle
                var menus = document.select("#chapterlist p a[href]")
                for ((index, menuItem) in menus.withIndex()) {
                    if (index >= book.bookMenu!!.size) {
                        var bookMenu = BookMenu()
                        bookMenu.name = menuItem.html()
                        bookMenu.link = menuItem.attr("href")
                        book.bookMenu!!.add(bookMenu)
                    }
                }
                book.save()
                for ((index,menuItem) in book.bookMenu!!.withIndex()){
                    if (request!!.url.toString().replace(CommonParams.base_url,"/").equals(menuItem.link)){
                        book.bookLook = index
                        var intent = Intent(this@MainActivity, BookContentActivity::class.java)
                        intent.putExtra("title", mBookTitle)
                        intent.putExtra("position", book.bookLook)
                        intent.putExtra("bookcode", mBookCode)
                        startActivity(intent)
                        book.save()
                    }
                }
            }
        })
    }
}
