package org.d3if0065.assessment.network

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.d3if0065.assessment.model.Article
import org.d3if0065.assessment.model.OpStatus
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path


private const val BASE_URL = "https://retoolapi.dev/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface ArticleService {
    @GET("jWK4w6/article")
    suspend fun getArticle(
        @Header("Authorization") userId: String
    ): List<Article>

    @POST("jWK4w6/article")
    suspend fun postArticle(@Body article: Article): Article

    @DELETE("jWK4w6/article/{id}")
    suspend fun deleteArticle(@Path("id") id: Long): OpStatus

}

object ArticleApi {
    val service: ArticleService by lazy {
        retrofit.create(ArticleService::class.java)
    }

    fun getArticleUrl(imageId: String): Bitmap? {
        return try {
            val decodedString = Base64.decode(imageId, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

enum class ApiStatus { LOADING, SUCCESS, FAILED}
