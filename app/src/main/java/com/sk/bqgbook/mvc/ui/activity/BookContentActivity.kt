package com.sk.bqgbook.mvc.ui.activity

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.StringUtils
import com.sk.bqgbook.R
import com.sk.bqgbook.app.CommonParams
import com.sk.bqgbook.app.Preference
import com.sk.bqgbook.app.net.NetCallback
import com.sk.bqgbook.app.net.NetUtils
import com.sk.bqgbook.mvc.adapter.BookMenuAdapter
import com.sk.bqgbook.mvc.model.BookMenu
import com.sk.bqgbook.mvc.model.Books
import com.vicpin.krealmextensions.query
import com.vicpin.krealmextensions.save
import kotlinx.android.synthetic.main.activity_book_content.*
import kotlinx.android.synthetic.main.activity_book_menu.*
import org.jsoup.nodes.Document

class BookContentActivity : AppCompatActivity(),View.OnTouchListener {
    var mTitle = ""
    var mPosition = 0
    lateinit var mBook: List<Books>
    var mMenuDialog: AlertDialog? = null
    lateinit var mAdapter: BookMenuAdapter
    var mBookCode = ""
    var fontsize_progress by Preference("fontsize", 0)
    lateinit var bookMenuDialogRV:RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_content)
        content_layout.setPadding(0, BarUtils.getStatusBarHeight() + controller.height, 0, 0)
        controller.setPadding(
            controller.paddingLeft,
            BarUtils.getStatusBarHeight() + controller.paddingTop,
            controller.paddingRight,
            controller.paddingBottom
        )
        mTitle = intent.getStringExtra("title")
        mPosition = intent.getIntExtra("position", 0)
        mBookCode = intent.getStringExtra("bookcode")
        mBook = Books().query { equalTo("book", mTitle) }
        BarUtils.setNavBarVisibility(this, false)
        font_size_sb.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                content_tv.textSize = progress * 1f / 100 * 30 + 14.5f
                fontsize_progress = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })
        content_tv.setOnTouchListener(this)
        preview.setOnClickListener {
            preview()
        }
        next.setOnClickListener {
            next()
        }
        menu.setOnClickListener {
            if (mMenuDialog == null) {
                var view = LayoutInflater.from(this).inflate(R.layout.book_menu_dialog, null)
                bookMenuDialogRV = view.findViewById(R.id.book_menu_dialog_recyclerview)
                bookMenuDialogRV.layoutManager = LinearLayoutManager(this)
                mAdapter = BookMenuAdapter()
                mAdapter.bindToRecyclerView(bookMenuDialogRV)
                mAdapter.setOnItemClickListener { adapter, view, position ->
                    if (position == 0) {
                        var viewHeight = view.height
                        bookMenuDialogRV.smoothScrollBy(0, viewHeight * (mAdapter.itemCount))
                        return@setOnItemClickListener
                    }
                    mPosition = position
                    mMenuDialog!!.dismiss()
                    setContent()
                }
                mMenuDialog = AlertDialog.Builder(this)
                    .setView(view)
                    .create()
            }
            mAdapter.setNewData(mBook[0].bookMenu)
            mMenuDialog!!.show()
            bookMenuDialogRV!!.layoutManager!!.scrollToPosition(mPosition)
        }
        controller.visibility = View.GONE
        checkButton()
        setContent()
    }

    private fun preview(){
        mPosition--
        content_tv.text = ""
        if (mPosition == 1) {
            preview.visibility = View.INVISIBLE
        } else {
            preview.visibility = View.VISIBLE
        }
        next.visibility = View.VISIBLE
        setContent()
    }

    private fun next(){
        mPosition++
        content_tv.text = ""
        if (mPosition == mBook[0].bookMenu!!.size - 1) {
            next.visibility = View.INVISIBLE
        } else {
            next.visibility = View.VISIBLE
        }
        preview.visibility = View.VISIBLE
        setContent()
    }

    private fun controllerStatusChange(){
        if (controller.visibility == View.VISIBLE) {
            controller.visibility = View.GONE
            BarUtils.setNavBarVisibility(this, false)
        } else {
            controller.visibility = View.VISIBLE
            BarUtils.setNavBarVisibility(this, true)
        }
    }

    private fun checkButton() {
        if (mPosition == 1) {
            preview.visibility = View.INVISIBLE
        } else {
            preview.visibility = View.VISIBLE
        }
        if (mPosition == mBook[0].bookMenu!!.size - 1) {
            next.visibility = View.INVISIBLE
        } else {
            next.visibility = View.VISIBLE
        }
    }

    private fun setContent() {
        var bookMenu = mBook.get(0).bookMenu!!.get(mPosition)
        content_title.text = mTitle + "-" + bookMenu!!.name
        if (!StringUtils.isEmpty(mBook.get(0).bookMenu!!.get(mPosition)!!.content)) {
            content_tv.text = Html.fromHtml(mBook.get(0).bookMenu!!.get(mPosition)!!.content).toString()
            content_progress_layout.visibility = View.GONE
            updateMenu()
            if (mPosition+1 <= mBook[0].bookMenu!!.size - 1) {
                preloadContent()
            }
            content_sv.scrollTo(0,0)
            return
        }
        NetUtils.getDocument(CommonParams.base_url + mBook.get(0).bookMenu!!.get(mPosition)!!.link,
            object : NetCallback {
                override fun onSuccess(document: Document) {
                    var bookContent = document.select("#chaptercontent").html()
                    mBook.get(0).bookMenu!!.get(mPosition)!!.content = bookContent
                    mBook.get(0).save()
                    content_tv.text = Html.fromHtml(bookContent)
                    content_tv.textSize = fontsize_progress * 1f / 100 * 30 + 14.5f
                    font_size_sb.progress = fontsize_progress
                    content_progress_layout.visibility = View.GONE
                    controller.visibility = View.GONE
                    updateMenu()
                    if (mPosition+1 <= mBook[0].bookMenu!!.size - 1) {
                        preloadContent()
                    }
                    content_sv.scrollTo(0,0)
                }
            })
    }

    private fun preloadContent(){
        if (!StringUtils.isEmpty(mBook.get(0).bookMenu!!.get(mPosition+1)!!.content)){
            return
        }
        NetUtils.getDocument(CommonParams.base_url + mBook.get(0).bookMenu!!.get(mPosition+1)!!.link,
            object : NetCallback {
                override fun onSuccess(document: Document) {
                    var bookContent = document.select("#chaptercontent").html()
                    mBook.get(0).bookMenu!!.get(mPosition+1)!!.content = bookContent
                    mBook.get(0).save()
                }
            })
    }

    override fun onDestroy() {
        super.onDestroy()
        mBook[0].bookLook = mPosition
        mBook[0].save()
    }
    var actionDownTime:Long = 0
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        when(event!!.action){
            MotionEvent.ACTION_DOWN->{
                actionDownTime = System.currentTimeMillis()
            }
            MotionEvent.ACTION_UP->{
                if (System.currentTimeMillis()-actionDownTime<200){
                    if (event.x>0&&event.x<=ScreenUtils.getScreenWidth()/3){
                        preview()
                    }else if(event.x>ScreenUtils.getScreenWidth()/3&&event.x<=ScreenUtils.getScreenWidth()/3*2){
                        controllerStatusChange()
                    }else if (event.x>ScreenUtils.getScreenWidth()/3*2&&event.x<=ScreenUtils.getScreenWidth()){
                        next()
                    }
                }
                return false
            }
        }
        return true
    }

    fun updateMenu() {
        NetUtils.getDocument(CommonParams.base_url + mBookCode + "/all.html", object : NetCallback {
            override fun onSuccess(document: Document) {
                var menus = document.select("#chapterlist p a[href]")
                for ((index, menuItem) in menus.withIndex()) {
                    if (index >= mBook[0].bookMenu!!.size) {
                        var bookMenu = BookMenu()
                        bookMenu.name = menuItem.html()
                        bookMenu.link = menuItem.attr("href")
                        mBook[0].bookMenu!!.add(bookMenu)
                    }
                }
                mBook[0].save()
            }
        })
    }
}
