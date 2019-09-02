package com.sk.bqgbook.mvc.adapter

import com.blankj.utilcode.util.StringUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.sk.bqgbook.R
import com.sk.bqgbook.mvc.model.BookMenu
import com.sk.bqgbook.mvc.ui.holder.BookMenuHolder

class BookMenuAdapter : BaseQuickAdapter<BookMenu,BookMenuHolder> {
    override fun convert(helper: BookMenuHolder, item: BookMenu?) {
        helper!!.mName.text = item!!.name
        if (StringUtils.isEmpty(item.content)){
            helper.mStatus.text = ""
        }else{
            helper.mStatus.text = "已缓存"
        }
    }

    constructor() : super(R.layout.book_menu_item)

}