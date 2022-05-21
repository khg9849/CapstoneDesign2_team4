package com.example.kakaotalktospeech

class RecyclerItem {
    private var title: String? = null
    private var content: Int? = null
    private var resId = 0

    fun getTitle(): String? {
        return title
    }

    fun setTitle(title: String?) {
        this.title = title
    }

    fun getContent(): Int? {
        return content
    }

    fun setContent(content: Int?) {
        this.content = content
    }

    fun getResId(): Int {
        return resId
    }

    fun setResId(resId: Int) {
        this.resId = resId
    }
}