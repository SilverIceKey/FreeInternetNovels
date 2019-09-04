package com.sk.bqgbook.mvc.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.BarUtils
import com.sk.bqgbook.R
import com.sk.bqgbook.mvc.adapter.BookAdapter
import com.sk.bqgbook.mvc.model.Books
import com.vicpin.krealmextensions.query
import com.vicpin.krealmextensions.queryAll
import kotlinx.android.synthetic.main.activity_book_menu.*

class BookShelfActivity : AppCompatActivity() {
    lateinit var mUrl: String
    lateinit var mAdapter: BookAdapter
    var mTitle = ""
    var mBookCode = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_menu)
        content.setPadding(0, BarUtils.getStatusBarHeight(), 0, 0)
        back.setOnClickListener { finish() }
        title_tv.text = "书架"
        recyclerview.layoutManager = LinearLayoutManager(this)
        mAdapter = BookAdapter()
        mAdapter.bindToRecyclerView(recyclerview)
        mAdapter.setOnItemClickListener { adapter, view, position ->
            var book = adapter.getItem(position) as Books
            var intent = Intent(this@BookShelfActivity, BookContentActivity::class.java)
            intent.putExtra("title", book.book)
            intent.putExtra("position", book.bookLook)
            intent.putExtra("bookcode",book.bookCode)
            startActivity(intent)
        }
        setList()
    }

    private fun setList() {
        var books = Books().queryAll()
        mAdapter.setNewData(books)

        progress_layout.visibility = View.GONE
    }
}