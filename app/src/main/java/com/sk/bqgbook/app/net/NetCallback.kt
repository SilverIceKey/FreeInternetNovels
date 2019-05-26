package com.sk.bqgbook.app.net

import org.jsoup.nodes.Document

interface NetCallback{
    fun onSuccess(document: Document)
}