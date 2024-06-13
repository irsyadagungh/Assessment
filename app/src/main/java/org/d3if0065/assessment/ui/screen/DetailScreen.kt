package org.d3if0065.assessment.ui.screen

import android.graphics.Color
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import org.d3if0065.assessment.R
import org.d3if0065.assessment.component.DisplayAlertDialog
import org.d3if0065.assessment.database.ArticleDb
import org.d3if0065.assessment.navigation.ScreenManager
import org.d3if0065.assessment.util.ViewModelFactory
import org.d3if0065.assessment.viewModel.DetailViewModel
import org.d3if0065.assessment.viewModel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(navController: NavHostController, id: Long? = null) {

    val context = LocalContext.current
    val db = ArticleDb.getInstance(context)
    val factory = ViewModelFactory(db.dao)
    val viewModel: DetailViewModel = viewModel()

    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }

    LaunchedEffect(true) {
        if (id == null) return@LaunchedEffect
        val data = viewModel.getArticle(id) ?: return@LaunchedEffect
        title = data.title
        content = data.content
        category = data.category
    }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val scrollState = rememberScrollState()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold (
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        topBar = {
            MediumTopAppBar(
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                title = {
                    Text(
                        text = title,
                        fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                        color = MaterialTheme.colorScheme.primary,
                    )
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(MaterialTheme.colorScheme.surfaceVariant),
                actions = {
                    IconButton(
                        onClick = {
                            navController.navigate(ScreenManager.EditArticle.withId(id!!))
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = stringResource(R.string.edit),
                            tint = MaterialTheme.colorScheme.primary
                        )


                    }
                    IconButton(
                        onClick = {
                            showDialog = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = stringResource(R.string.delete),
                            tint = MaterialTheme.colorScheme.error
                        )
                        DisplayAlertDialog(
                            openDialog =  showDialog,
                            onDismissRequest = { showDialog = false }
                        ) {
                            showDialog = false
                            viewModel.delete(id!!)
                            navController.popBackStack()
                        }
                    }
                }
            )
        }
    ){paddingValues ->
                DetailArticle(
                    title = title,
                    content = content,
                    category = category,
                    modifier = Modifier.padding(paddingValues),
                    scrollState = scrollState
                )
    }
}

@Composable
fun DetailArticle(
    title: String,
    content: String,
    category: String,
    modifier: Modifier,
    scrollState: ScrollState
) {

    var color = @androidx.compose.runtime.Composable {
        when (category) {
            stringResource(R.string.entertaiment) -> MaterialTheme.colorScheme.secondary
            stringResource(R.string.music) -> MaterialTheme.colorScheme.primary
            stringResource(R.string.sport) -> MaterialTheme.colorScheme.tertiary
            else -> MaterialTheme.colorScheme.primary
        }
    }

    Column (
        modifier = modifier
            .padding(16.dp)
            .verticalScroll(scrollState)
    ){
        Text(
            text = category,
            textAlign = TextAlign.Start,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .background(
                    color = getColorByCategory(category = category),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(horizontal = 8.dp, vertical = 4.dp),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = content,
            textAlign = TextAlign.Justify,
        )
    }
}

@Composable
fun getColorByCategory(category: String): androidx.compose.ui.graphics.Color {
    return when (category) {
        stringResource(R.string.entertaiment) -> MaterialTheme.colorScheme.inversePrimary
        stringResource(R.string.music) -> MaterialTheme.colorScheme.primary
        stringResource(R.string.sport) -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.primary
    }
}


