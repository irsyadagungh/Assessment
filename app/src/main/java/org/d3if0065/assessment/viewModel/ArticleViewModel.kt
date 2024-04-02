package org.d3if0065.assessment.viewModel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import org.d3if0065.assessment.model.Article

class ArticleViewModel: ViewModel() {
    val data = mutableStateListOf<Article>()
    val list = mutableStateListOf(arrayOf("sport", "entertaiment", "music"))
}