package org.d3if0065.assessment.ui.screen

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import org.d3if0065.assessment.R
import org.d3if0065.assessment.model.Article
import org.d3if0065.assessment.navigation.ScreenManager
import org.d3if0065.assessment.viewModel.ArticleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(navController: NavHostController = rememberNavController()) {

    val viewModel: ArticleViewModel = viewModel()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.home),
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                actions = {
                          IconButton(onClick = { navController.navigate(ScreenManager.About.route) }) {
                              Icon(
                                  imageVector = Icons.Outlined.Info,
                                  contentDescription = "info",
                                  tint = MaterialTheme.colorScheme.onPrimary
                              )
                          }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(MaterialTheme.colorScheme.primary)
            )
        },

    ) { padding ->
        HomeContent(modifier = Modifier.padding(padding), viewModel = viewModel)
    }
}

@Composable
fun HomeContent(modifier: Modifier, viewModel: ArticleViewModel) {
    val title = rememberSaveable {
        mutableStateOf("")
    }
    val titleError = remember {
        mutableStateOf(false)
    }
    val content = rememberSaveable {
        mutableStateOf("")
    }
    val contentError = remember {
        mutableStateOf(false)
    }
    val category = rememberSaveable {
        mutableStateOf("")
    }
    val categoryError = remember {
        mutableStateOf(false)
    }
    val data = viewModel.data

    val menu = remember {
        mutableStateOf(false)
    }

    val textFieldSize = remember {
        mutableStateOf(Size.Zero)
    }

    val icon = if (menu.value){
        Icons.Filled.KeyboardArrowUp
    } else {
        Icons.Filled.KeyboardArrowDown
    }

    val context = LocalContext.current

    val categoryList = remember {
        mutableStateListOf<String>().apply {
            addAll(listOf(
                context.getString(R.string.sport),
                context.getString(R.string.entertaiment),
                context.getString(R.string.music)
            ))
        }
    }


    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = stringResource(R.string.title)) },
            value = title.value,
            onValueChange = { title.value = it },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            trailingIcon = {
                IconPicker(isError = titleError.value)
            },
            supportingText = {
                ErrorHint(isError = titleError.value)
            },
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = stringResource(R.string.content)) },
            value = content.value,
            onValueChange = { content.value = it },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Default
            ),
            maxLines = 4,
            singleLine = false,
            trailingIcon = {
                IconPicker(isError = contentError.value)
            },
            supportingText = {
                ErrorHint(isError = contentError.value)
            }
        )

        Column (
            modifier = Modifier.padding(top = 8.dp)
        ){
            OutlinedTextField(
                enabled = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        textFieldSize.value = coordinates.size.toSize()
                    },
                value = category.value,
                onValueChange = {
                    category.value = it
                    menu.value = !menu.value
                                },
                supportingText = {
                    ErrorHint(isError = categoryError.value)
                },
                trailingIcon = {
                    Icon(imageVector = icon, contentDescription = stringResource(R.string.cate), modifier = Modifier.clickable { menu.value = !menu.value })
                },
                label = { Text(text = stringResource(R.string.category1)) },
                colors = OutlinedTextFieldDefaults.colors(
                    disabledBorderColor = MaterialTheme.colorScheme.primary,
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurface,
                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurface,
                    disabledSupportingTextColor = MaterialTheme.colorScheme.onSurface
                )
            )
            
            DropdownMenu(
                expanded = menu.value,
                onDismissRequest = { menu.value = false },
                modifier = Modifier.width(with(LocalDensity.current) { textFieldSize.value.width.toDp() })
            ) {
                for (i in categoryList.indices){
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = categoryList[i])
                               },
                        onClick = {
                            category.value = categoryList[i]
                        })
                }
            }
        }

        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ){
            OutlinedButton(
                onClick = {
                title.value = ""
                content.value = ""
                category.value = ""
                },
            ) {
                Text(text = stringResource(R.string.reset))
            }
            
            Spacer(modifier = Modifier.width(20.dp))
            
            Button(onClick = {
                titleError.value = title.value == ""
                contentError.value = content.value == ""
                categoryError.value = category.value == ""
                if (titleError.value || contentError.value || categoryError.value) {
                    return@Button
                }
                add(title.value, content.value, category.value, data)

                title.value = ""
                content.value = ""
                category.value = ""
            }) {
                Text(text = stringResource(R.string.submit))
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            if (data.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.empty),
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } else {
                items(data) { item ->
                    ListItem(item.title, item.content, item.category, data.indexOf(item), viewModel)
                }
            }
        }

    }
}

@Composable
fun ListItem(title: String, content: String, category: String, index: Int, viewModel: ArticleViewModel) {

    val context = LocalContext.current
    val data = viewModel.data


    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondaryContainer),
            elevation = CardDefaults.cardElevation(0.2.dp)
        ) {
            Text(
                modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
                text = title,
                fontWeight = FontWeight.Bold
            )
            Text(
                modifier = Modifier.padding(16.dp),
                text = content
            )

            Divider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(modifier = Modifier.padding(16.dp), text = stringResource(R.string.category) + ": " + category)

                Row (
                    horizontalArrangement = Arrangement.End
                ){
                    IconButton(
                        onClick = {
                            shareData(
                                context = context,
                                message = context.getString(
                                    R.string.share_content,
                                    title, content, category,
                                )
                            )
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Share,
                            contentDescription = stringResource(R.string.share)
                        )
                    }
                    IconButton(
                        onClick = {
                            delete(index, data)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = stringResource(R.string.delete),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

fun add(title: String, content: String, category: String, list: MutableList<Article>) {
    list.add(Article(title, content, category))
}

fun delete(index: Int, list: MutableList<Article>){
    list.removeAt(index)
}

@Composable
fun IconPicker(isError: Boolean) {
    if (isError){
        Icon(imageVector = Icons.Filled.Warning, contentDescription = null)
    }
}

@Composable
fun ErrorHint(isError: Boolean) {
    if (isError){
        Text(text = stringResource(R.string.input_invalid))
    }
}

private fun shareData(context: Context, message: String){
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, message)
    }
    if (shareIntent.resolveActivity(context.packageManager) != null){
        context.startActivity(shareIntent)
    }
}

@Preview
@Composable
private fun PreviewHome() {
    Home(navController = rememberNavController())
}
