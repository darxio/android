package com.darx.foodscaner.services

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

class ApiCookieJar : CookieJar {

    private val cookies = mutableListOf<Cookie>()

    override fun saveFromResponse(url: HttpUrl, cookieList: List<Cookie>) {
        cookies.clear()
        cookies.addAll(cookieList)
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> = cookies
}