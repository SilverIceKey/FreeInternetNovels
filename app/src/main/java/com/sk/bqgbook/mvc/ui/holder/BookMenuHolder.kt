package com.sk.bqgbook.mvc.ui.holder

import android.view.View
import android.widget.TextView
import com.chad.library.adapter.base.BaseViewHolder
import com.sk.bqgbook.R
import kotlinx.android.synthetic.main.book_menu_item.view.*

class BookMenuHolder(view: View?) : BaseViewHolder(view) {
    var mName:TextView = view!!.findViewById(R.id.name)
    var mStatus:TextView = view!!.findViewById(R.id.status)
}