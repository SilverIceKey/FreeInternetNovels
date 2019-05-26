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
import com.sk.bqgbook.R
import com.sk.bqgbook.app.CommonParams
import com.sk.bqgbook.app.net.NetResponseCallback
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
            //根据网站的路径得知只要后缀为all.html即为目录对url进行匹配
            if (request!!.url.toString().endsWith("all.html")) {
                //首先查找数据库是否存在对应的小说的数据，如果存在查看观看进度是否存在，因为目录第0项是滚动到底部，刚好可以使用
                var book = Books().query { equalTo("book",mBookTitle) }
                if (book.size!=0){
                    //如果观看不为0跳转阅读界面，否在跳转目录界面，因为只记录到章节，没有记录到具体行，对不起.jpg
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
            //因为网站有最新章节的直接跳转，因此对最新章节的链接规则进行匹配，mBookCode的获取在onPageFinished方法中,这里对数据库进行查找，存在小说直接跳转阅读界面，否则根据目录链接规则先存储并更新小说的目录信息。
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
            //设置进行图片加载
            agentWeb.webCreator.webView.settings.blockNetworkImage = false
            //根据链接规则获取书本在链接中的code
            if (!url!!.endsWith(CommonParams.base_url)&&!url.endsWith("html")){
                mBookCode = url!!.replace(CommonParams.base_url,"").replace("/","")
            }
            //js交互获取header标签的背景颜色并使用BarUtils设置顶部导航的颜色
            view!!.loadUrl("javascript:window.android.setBarColor($('header').css('background-color'));")
            //js交互获取小说的标题
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
        //用于延迟加载图片，更快显示页面
        agentWeb.webCreator.webView.settings.blockNetworkImage = true
        //进行js注册
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
        NetUtils.getInstance()!!.getMenu(mBookCode, object : NetResponseCallback {
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
