package reson.chocomedia.util

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class HttpUtil{
    companion object {
        fun getDataSting(requestUrl: String): String {
            var responseString = ""
            val client: OkHttpClient = OkHttpClient().newBuilder().build()
            val request: Request = Request.Builder().url(requestUrl).get().build()
            var response : Response =  client.newCall(request).execute()
            response.body?.run {
                responseString = string()
            }
            return responseString
        }
    }
}