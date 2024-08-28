package com.teavaro.ecommDemoApp.core.utils

import android.content.ClipData
import android.content.Context
import android.os.Build
import android.text.ClipboardManager
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.Base64

internal object StringUtils {

    fun stringToSha256String(string: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(string.encodeToByteArray())
        return bytes.fold("") { str, it -> str + "%02x".format(it) }
    }

    fun decodeURL(string: String): String {
        return URLDecoder.decode(string, StandardCharsets.UTF_8.toString())
    }

    fun setClipboard(context: Context, text: String) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.text = text
        } else {
            val clipboard =
                context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
            val clip = ClipData.newPlainText("Copied Text", text)
            clipboard.setPrimaryClip(clip)
        }
    }
}