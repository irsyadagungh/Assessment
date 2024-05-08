package org.d3if0065.assessment.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.d3if0065.assessment.database.ArticleDao
import org.d3if0065.assessment.model.Article

class DetailViewModel(private val dao: ArticleDao): ViewModel() {

    fun insert(title: String, content: String, category: String){
        val article = Article(
            title = title,
            content = content,
            category = category
        )

        viewModelScope.launch {
            dao.insert(article)
        }
    }

    suspend fun getArticle(id: Long): Article?{
        return dao.getArticleById(id)
    }

    fun update(id: Long, title: String, content: String, category: String){
        val article = Article(
            id = id,
            title = title,
            content = content,
            category = category
        )

        viewModelScope.launch(Dispatchers.IO) {
            dao.update(article)
        }
    }

    fun delete(id: Long){
        viewModelScope.launch(Dispatchers.IO) {
            dao.delete(id)
        }
    }
}