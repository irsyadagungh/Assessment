package org.d3if0065.assessment.ui.screen

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import org.d3if0065.assessment.R
import org.d3if0065.assessment.component.DisplayAlertDialog
import org.d3if0065.assessment.database.ArticleDb
import org.d3if0065.assessment.navigation.ScreenManager
import org.d3if0065.assessment.util.ViewModelFactory
import org.d3if0065.assessment.viewModel.DetailViewModel
import org.d3if0065.assessment.viewModel.MainViewModel

const val ID_ARTICLE = "id_article"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormArticle(navController: NavHostController, id: Long? = null) {

    val context = LocalContext.current
    val db = ArticleDb.getInstance(context)
    val factory = ViewModelFactory(db.dao)
    val viewModel: DetailViewModel = viewModel(factory = factory)
    val viewModel2: MainViewModel = viewModel(factory = factory)
    val data by viewModel2.data.collectAsState()

    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }

    val categoryList = listOf(
        stringResource(R.string.music),
        stringResource(R.string.sport),
        stringResource(R.string.entertaiment),
    )

    LaunchedEffect(true) {
        if (id == null) return@LaunchedEffect
        val data = viewModel.getArticle(id) ?: return@LaunchedEffect
        title = data.title
        content = data.content
        category = data.category
    }

    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                title = {
                    if (id != null){
                        Text(text = stringResource(id = R.string.edit), color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Text(text = stringResource(id = R.string.add), color = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                actions = {
                    if (id != null){
                        DeleteAction {
                            showDialog = true
                        }
                        DisplayAlertDialog(
                            openDialog = showDialog,
                            onDismissRequest = { showDialog = false },
                        ) {
                            showDialog = false
                            viewModel.delete(id)
                            navController.popBackStack()
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(MaterialTheme.colorScheme.primary)
            )
        },

        ) { padding ->
        Form(
            title = title,
            onTitleChange = { title = it },
            content = content,
            onContentChange = { content = it },
            category = category,
            onCategoryChange = { category = it },
            context = context,
            listCategory = categoryList,
            onClickSubmit = {
                 if (id == null){
                     viewModel.insert(title, content, category)
                 } else {
                     viewModel.update(id, title, content, category)
                 }
                navController.navigate(ScreenManager.Home.route)
            },
            modifier = Modifier.padding(padding),
        )
    }
}

@Composable
fun Form(
    title: String, onTitleChange: (String) -> Unit,
    content: String, onContentChange: (String) -> Unit,
    category: String, onCategoryChange: (String) -> Unit,
    context: Context,
    listCategory: List<String>,
    onClickSubmit: () -> Unit,
    modifier: Modifier,
) {


    val titleError = remember { mutableStateOf(false) }
    val contentError = remember { mutableStateOf(false) }
    val categoryError = remember { mutableStateOf(false) }

    val menu = remember { mutableStateOf(false) }
    val textFieldSize = remember { mutableStateOf(Size.Zero) }

    val icon = if (menu.value){
        Icons.Filled.KeyboardArrowUp
    } else {
        Icons.Filled.KeyboardArrowDown
    }


    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = stringResource(R.string.title)) },
            value = title,
            onValueChange = { onTitleChange(it) },
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
            value = content,
            onValueChange = { onContentChange(it) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Default
            ),
            minLines = 20,
            maxLines = 20,
            singleLine = false,
            trailingIcon = {
                IconPicker(isError = contentError.value)
            },
            supportingText = {
                ErrorHint(isError = contentError.value)
            }
        )

        Column(
            modifier = Modifier.padding(top = 8.dp)
        ) {
            OutlinedTextField(
                enabled = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        textFieldSize.value = coordinates.size.toSize()
                    },
                value = category,
                onValueChange = {
                    onContentChange(it)
                    menu.value = !menu.value
                },
                supportingText = {
                    ErrorHint(isError = categoryError.value)
                },
                trailingIcon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = stringResource(R.string.cate),
                        modifier = Modifier.clickable { menu.value = !menu.value })
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
                for (i in listCategory.indices) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = listCategory[i]
                            )
                        },
                        onClick = {
                            onCategoryChange(listCategory[i])
                            menu.value = false
                        })
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            OutlinedButton(
                onClick = {
                    onCategoryChange("")
                    onContentChange("")
                    onTitleChange("")
                },
            ) {
                Text(text = stringResource(R.string.reset))
            }

            Spacer(modifier = Modifier.width(20.dp))

            Button(onClick = {
                titleError.value = title == ""
                contentError.value = content == ""
                categoryError.value = category == ""
                if (titleError.value || contentError.value || categoryError.value) {
                    Toast.makeText(context, R.string.invalid, Toast.LENGTH_LONG).show()
                    return@Button
                }

                onClickSubmit()

                onTitleChange("")
                onContentChange("")
                onCategoryChange("")
            }) {
                Text(text = stringResource(R.string.submit))
            }
        }
    }
}

@Composable
fun DeleteAction(delete: () -> Unit) {

    var expanded by remember { mutableStateOf(false) }

    IconButton(onClick = { expanded = true }) {
        Icon(
            imageVector = Icons.Filled.MoreVert,
            contentDescription = stringResource(id = R.string.more),
            tint = MaterialTheme.colorScheme.onPrimary
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text(text = stringResource(id = R.string.delete)) },
                onClick = {
                    expanded = false
                    delete()
                })
        }
    }
}
