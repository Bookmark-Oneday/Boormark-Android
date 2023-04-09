package com.bookmark.bookmark_oneday.app.retrofit

import android.annotation.SuppressLint
import com.bookmark.bookmark_oneday.data.datasource.token_datasource.TokenDataSource
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class BookMarkOneDayRetrofit @Inject constructor(
    private val tokenDataSource: TokenDataSource
) {
    private lateinit var retrofit : Retrofit

    fun init() {
        if (!::retrofit.isInitialized) {
            val okhttpClient = getUnsafeOkHttpClient(tokenDataSource)

            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okhttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }

    private fun getOkHttpClient(tokenDataSource: TokenDataSource): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(10000L, TimeUnit.MILLISECONDS)
            .writeTimeout(10000L, TimeUnit.MILLISECONDS)
            .readTimeout(5000L, TimeUnit.MILLISECONDS)
            .addInterceptor(NetworkInterceptor(tokenDataSource::accessToken))
            .build()
    }

    fun getRetrofit() : Retrofit {
        return retrofit
    }

    companion object {
        private const val BASE_URL = "https://api.bmonlner.me"
    }

    // TODO SSL 인증서 발급 이후 상단의 getOkHttpClient 를 사용하도록 변경
    private fun getUnsafeOkHttpClient(tokenDataSource: TokenDataSource): OkHttpClient {
        val trustAllCerts = arrayOf<TrustManager>(@SuppressLint("CustomX509TrustManager") object : X509TrustManager {
            @SuppressLint("TrustAllX509TrustManager")
            override fun checkClientTrusted(
                chain: Array<out java.security.cert.X509Certificate>?,
                authType: String?
            ) {

            }

            @SuppressLint("TrustAllX509TrustManager")
            override fun checkServerTrusted(
                chain: Array<out java.security.cert.X509Certificate>?,
                authType: String?
            ) {

            }

            override fun getAcceptedIssuers(): Array<out java.security.cert.X509Certificate> {
                return arrayOf()
            }
        })

        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, SecureRandom())

        val sslSocketFactory = sslContext.socketFactory

        return OkHttpClient.Builder()
            .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier { hostname, session -> true }
            .connectTimeout(10000L, TimeUnit.MILLISECONDS)
            .writeTimeout(10000L, TimeUnit.MILLISECONDS)
            .readTimeout(5000L, TimeUnit.MILLISECONDS)
            .addInterceptor(NetworkInterceptor(tokenDataSource::accessToken))
            .build()
    }
}