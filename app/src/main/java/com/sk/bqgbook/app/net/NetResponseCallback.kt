package com.sk.bqgbook.app.net

import org.jsoup.nodes.Document

interface NetResponseCallback{
    fun onSuccess(document: Document)
}