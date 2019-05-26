package com.sk.bqgbook.mvc.model

import io.realm.RealmObject

open class BookMenu : RealmObject() {
    var name: String = ""
    var link: String = ""
    var content: String = ""
}