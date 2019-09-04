package com.sk.bqgbook.mvc.adapter

import com.blankj.utilcode.util.StringUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.sk.bqgbook.R
import com.sk.bqgbook.mvc.model.BookMenu
import com.sk.bqgbook.mvc.model.Books
import com.sk.bqgbook.mvc.ui.holder.BookMenuHolder

class BookAdapter: BaseQuickAdapter<Books, BookMenuHolder> {
    override fun convert(helper: BookMenuHolder, item: Books?) {
        helper!!.mName.text = item!!.book
        helper.mStatus.text ="第"+item.bookLook.toString()+"章"
    }

    constructor() : super(R.layout.book_menu_item)
}