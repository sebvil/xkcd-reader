package com.colibrez.xkcdreader.android.util.webpage

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent

class InBrowserWebPageViewer(private val context: Context) : WebPageViewer {

    override fun viewWebPage(webpageUrl: String) {
        val customTabsIntent = CustomTabsIntent.Builder().build()

        customTabsIntent.launchUrl(context, Uri.parse(webpageUrl))
    }
}
