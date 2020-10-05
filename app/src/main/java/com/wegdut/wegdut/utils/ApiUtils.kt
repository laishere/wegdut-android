package com.wegdut.wegdut.utils

import android.content.Context
import com.franmontiel.persistentcookiejar.ClearableCookieJar
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.google.gson.*
import com.wegdut.wegdut.BuildConfig
import com.wegdut.wegdut.MyException
import com.wegdut.wegdut.config.Config
import com.wegdut.wegdut.data.ResultWrapper
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.CookieManager
import java.util.*
import java.util.concurrent.TimeUnit

object ApiUtils {

    val gson: Gson = GsonBuilder()
        .registerTypeAdapter(Date::class.java, JsonDeserializer { json, _, _ ->
            Date(json.asLong)
        })
        .registerTypeAdapter(Date::class.java, JsonSerializer<Date> { src, _, _ ->
            JsonPrimitive(src.time)
        })
        .setLenient()
        .create()

    lateinit var okHttpClient: OkHttpClient

    private lateinit var retrofit: Retrofit
    private lateinit var cookieJar: ClearableCookieJar

    fun initApi(context: Context) {
        cookieJar = PersistentCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(context))
        val clientBuilder = OkHttpClient.Builder()
            .cookieJar(cookieJar)
            .readTimeout(40L, TimeUnit.SECONDS)
        if (BuildConfig.DEBUG) {
            val httpLoggingInterceptor = HttpLoggingInterceptor()
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.HEADERS
            clientBuilder
                .addInterceptor(httpLoggingInterceptor)
        }
        okHttpClient = clientBuilder.build()
        retrofit = Retrofit.Builder()
            .baseUrl(Config.apiBaseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()
    }

    fun initTest() {
        val cookieHandler = CookieManager()
        okHttpClient = OkHttpClient.Builder()
            .cookieJar(JavaNetCookieJar(cookieHandler))
            .build()
        retrofit = Retrofit.Builder()
            .baseUrl(Config.apiBaseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()
    }

    fun <T> create(clazz: Class<T>): T {
        return retrofit.create(clazz)
    }

    fun <T> handleCall(call: Call<T>): T {
        val rsp = call.execute()
        if (rsp.isSuccessful) return rsp.body()!!
        throw MyException(getHttpError(rsp.code()))
    }

    fun <T> handleResultWrapper(call: Call<ResultWrapper<T>>): T {
        val res = handleCall(call)
        if (res.ok()) return res.data
        throw MyException(res.msg ?: "发生错误")
    }

    fun <T> Call<T>.extractCall(): T {
        return handleCall(this)
    }

    fun <T> Call<ResultWrapper<T>>.extract(): T {
        return handleResultWrapper(this)
    }

    fun getHttpError(code: Int): String {
        return when (code) {
            400, 405 -> "API请求失效，请更新到更高版本"
            401, 403 -> "未登录或权限不足"
            404 -> "请求不存在"
            500 -> "服务器错误"
            502 -> "网关错误"
            503 -> "服务暂不可用"
            else -> "请求出错 $code"
        }
    }
}