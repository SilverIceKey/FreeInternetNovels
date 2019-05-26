package com.sk.bqgbook.mvc.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.BarUtils
import com.sk.bqgbook.R
import com.sk.bqgbook.app.net.NetResponseCallback
import com.sk.bqgbook.app.net.NetUtils
import com.sk.bqgbook.mvc.adapter.BookMenuAdapter
import com.sk.bqgbook.mvc.model.BookMenu
import com.sk.bqgbook.mvc.model.Books
import com.vicpin.krealmextensions.query
import com.vicpin.krealmextensions.queryAll
import com.vicpin.krealmextensions.save
import kotlinx.android.synthetic.main.activity_book_menu.*
import org.jsoup.nodes.Document

class BookMenuActivity : AppCompatActivity() {
    lateinit var mUrl: String
    lateinit var mAdapter: BookMenuAdapter
    var mTitle = ""
    var mBookCode = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_menu)
        content.setPadding(0, BarUtils.getStatusBarHeight(), 0, 0)
        mUrl = intent.getStringExtra("url")
        mTitle = intent.getStringExtra("title")
        mBookCode = intent.getStringExtra("bookcode")
        back.setOnClickListener { finish() }
        recyclerview.layoutManager = LinearLayoutManager(this)
        mAdapter = BookMenuAdapter()
        mAdapter.bindToRecyclerView(recyclerview)
        mAdapter.setOnItemClickListener { adapter, view, position ->
            if (position == 0) {
                var viewHeight = view.height
                recyclerview.smoothScrollBy(0, viewHeight * (mAdapter.itemCount))
                return@setOnItemClickListener
            }
            var intent = Intent(this@BookMenuActivity, BookContentActivity::class.java)
            intent.putExtra("title", mTitle)
            intent.putExtra("position", position)
            intent.putExtra("bookcode", mBookCode)
            startActivity(intent)
        }
        refreshlayout.setOnRefreshListener {
            progress_layout.visibility = View.VISIBLE
            setList()
        }
        refreshlayout.setNoMoreData(true)
        setList()
    }

    private fun setList() {
        var books = Books().query { equalTo("book", mTitle) }
        title_tv.text = mTitle
        if (books.size != 0) {
            mAdapter.setNewData(books[0].bookMenu)
            progress_layout.visibility = View.GONE
        }
        NetUtils.getInstance()!!.getMenu(mBookCode, object : NetResponseCallback {
            override fun onSuccess(document: Document) {
                lateinit var book: Books
                if (books.size == 0) {
                    book = Books()
                    book.id = Books().queryAll().size.toLong()
                    book.bookCode = mBookCode
                } else {
                    book = books.get(0)
                }
                //因为书名在<span class="title">的标签下，所以使用以下代码获得书名
                var titles = document.select("span.title")
                title_tv.text = titles.get(0).html()
                mTitle = titles.get(0).html()
                book.book = titles.get(0).html()
                //以下也是根据网页规则在id为chapterlist的标签下的<p>标签的<a>中的链接和章节名称（表述不好见谅）
                var menus = document.select("#chapterlist p a[href]")
                for ((index,menuItem) in menus.withIndex()) {
                    if (index>=book.bookMenu!!.size){
                        var bookMenu = BookMenu()
                        bookMenu.name = menuItem.html()
                        bookMenu.link = menuItem.attr("href")
                        book.bookMenu!!.add(bookMenu)
                    }
                }
                book.save()
                mAdapter.setNewData(book.bookMenu)
                refreshlayout.finishRefresh()
                progress_layout.visibility = View.GONE
            }
        })
    }
}
