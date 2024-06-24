package org.d3if0065.assessment.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import org.d3if0065.assessment.model.Article

@Dao
interface ArticleDao {

    @Insert
    suspend fun insert(article: Article)

    @Update
    suspend fun update(article: Article)

    @Query("SELECT * FROM article ORDER BY id DESC")
    fun getArticle(): Flow<List<Article>>

    @Query("SELECT * FROM article WHERE id = :id")
    suspend fun getArticleById(id: Long): Article?

    @Query("DELETE FROM article WHERE id = :id")
    suspend fun delete(id: Long)

}