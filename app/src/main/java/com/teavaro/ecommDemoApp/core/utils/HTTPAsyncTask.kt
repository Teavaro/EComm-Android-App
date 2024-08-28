package com.teavaro.ecommDemoApp.core.utils

import android.os.AsyncTask

class HTTPAsyncTask(private val action: (()->Unit)) : AsyncTask<String, Void, String>() {
    override fun doInBackground(vararg items: String?): String {
        action.invoke()
        return ""
    }
    override fun onPostExecute(result: String?) {
        action.invoke()
    }
}