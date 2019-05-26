package com.sk.bqgbook.mvc.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Books(
    @PrimaryKey
    var id: Long = 0,
    var book: String = "",
    var bookCode:String = "",
    var bookMenu: RealmList<BookMenu>? = RealmList(),
    var bookLook: Int = 0
) : RealmObject() {

}