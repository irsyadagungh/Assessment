package org.d3if0065.assessment.ui.screen

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.d3if0065.assessment.R
import org.d3if0065.assessment.component.DisplayAlertDialog
import org.d3if0065.assessment.database.ArticleDb
import org.d3if0065.assessment.model.Article
import org.d3if0065.assessment.model.User
import org.d3if0065.assessment.navigation.ScreenManager
import org.d3if0065.assessment.network.UserDataStore
import org.d3if0065.assessment.util.ViewModelFactory
import org.d3if0065.assessment.viewModel.DetailViewModel
import org.d3if0065.assessment.viewModel.MainViewModel
import java.io.ByteArrayOutputStream

const val ID_ARTICLE = "id_article"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormArticle(navController: NavHostController, id: Long? = null) {



    val context = LocalContext.current
    val viewModel: MainViewModel = viewModel()
    val userDataStore: UserDataStore = UserDataStore(context)
    val user by userDataStore.userFlow.collectAsState(User())

    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var bitmap: Bitmap? by remember { mutableStateOf(null) }

    val categoryList = listOf(
        stringResource(R.string.music),
        stringResource(R.string.sport),
        stringResource(R.string.entertaiment),
    )

    var showDialog by remember { mutableStateOf(false) }

    var stringImage = ""
    val launcher = rememberLauncherForActivityResult(CropImageContract()){
        bitmap = getCroppedImage(context.contentResolver, it)
        if (bitmap != null) {
            showDialog = true
            stringImage = bitmapToString(bitmap!!)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                title = {
                    Text(
                        text = if (id != null) stringResource(R.string.edit) else stringResource(R.string.add),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                actions = {
                    if (id != null) {
                        DeleteAction { showDialog = true }
                        DisplayAlertDialog(
                            openDialog = showDialog,
                            onDismissRequest = { showDialog = false },
                        ) {
                            showDialog = false
                            // viewModel.delete(id)
                            navController.popBackStack()
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(MaterialTheme.colorScheme.primary)
            )
        }
    ) { padding ->
        Form(
            title = title,
            onTitleChange = { title = it },
            content = content,
            onContentChange = { content = it },
            category = category,
            onCategoryChange = { category = it },
            bitmap = bitmap?: Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888),
            context = context,
            listCategory = categoryList,
            onClickImage = {
                val options = CropImageContractOptions(
                    null, CropImageOptions(
                        imageSourceIncludeGallery = true,
                        imageSourceIncludeCamera = true,
                        fixAspectRatio = true,
                    ),
                )
                launcher.launch(options)
            },
            onClickSubmit = {
                CoroutineScope(Dispatchers.IO).launch {
                    Log.d("BITMAP", "Success $stringImage")
                    viewModel.saveData(
                        Article(
                            title = title,
                            content = content,
                            category = category,
                            idUser = user.email,
                            image = stringImage
                        )
                    )
                }

                    navController.navigate(ScreenManager.Home.route)

            },
            modifier = Modifier.padding(padding),
        )
    }
}

@Composable
fun Form(
    title: String,
    onTitleChange: (String) -> Unit,
    content: String,
    onContentChange: (String) -> Unit,
    category: String,
    bitmap: Bitmap,
    onClickImage: () -> Unit,
    onCategoryChange: (String) -> Unit,
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

    val icon = if (menu.value) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = stringResource(R.string.title)) },
            value = title,
            onValueChange = onTitleChange,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            trailingIcon = { IconPicker(isError = titleError.value) },
            supportingText = { ErrorHint(isError = titleError.value) },
        )
        Row {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            IconButton(onClick = { onClickImage() }) {
                Icon(painter = painterResource(id = R.drawable.add_photo), contentDescription = null)
            }
        }
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = stringResource(R.string.content)) },
            value = content,
            onValueChange = onContentChange,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Default
            ),
            maxLines = 5,
            singleLine = false,
            trailingIcon = { IconPicker(isError = contentError.value) },
            supportingText = { ErrorHint(isError = contentError.value) }
        )

        Column(modifier = Modifier.padding(top = 8.dp)) {
            OutlinedTextField(
                enabled = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        textFieldSize.value = coordinates.size.toSize()
                    },
                value = category,
                onValueChange = onCategoryChange,
                supportingText = { ErrorHint(isError = categoryError.value) },
                trailingIcon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = stringResource(R.string.category),
                        modifier = Modifier.clickable { menu.value = !menu.value }
                    )
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
                listCategory.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(text = category) },
                        onClick = {
                            onCategoryChange(category)
                            menu.value = false
                        }
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            OutlinedButton(onClick = {
                onCategoryChange("")
                onContentChange("")
                onTitleChange("")
            }) {
                Text(text = stringResource(R.string.reset))
            }

            Spacer(modifier = Modifier.width(20.dp))

            Button(onClick = {
                titleError.value = title.isBlank()
                contentError.value = content.isBlank()
                categoryError.value = category.isBlank()
                if (titleError.value || contentError.value || categoryError.value) {
                    Toast.makeText(context, R.string.invalid, Toast.LENGTH_LONG).show()
                    return@Button
                }

                onClickSubmit()
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

private fun getCroppedImage(
    resolver: ContentResolver,
    result: CropImageView.CropResult
): Bitmap? {
    if (!result.isSuccessful) {
        Log.e("IMAGE", "Error ${result.error}")
        return null
    }

    val uri = result.uriContent ?: return null

    return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
        MediaStore.Images.Media.getBitmap(resolver, uri)
    } else {
        val source = ImageDecoder.createSource(resolver, uri)
        ImageDecoder.decodeBitmap(source)
    }
}

fun stringToBitmap(encodedString: String): Bitmap? {
    return try {
        val decodedString = Base64.decode(encodedString, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
fun bitmapToString(bitmap: Bitmap, format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG, quality: Int = 10): String {
    // Resize the bitmap if necessary
    val resizedBitmap = resizeBitmap(bitmap, maxWidth = 500, maxHeight = 500)

    return ByteArrayOutputStream().use { byteArrayOutputStream ->
        resizedBitmap.compress(format, quality, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }
}

fun resizeBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
    val width = bitmap.width
    val height = bitmap.height

    val aspectRatio = width.toFloat() / height.toFloat()
    val newWidth: Int
    val newHeight: Int

    if (width > height) {
        newWidth = maxWidth
        newHeight = (newWidth / aspectRatio).toInt()
    } else {
        newHeight = maxHeight
        newWidth = (newHeight * aspectRatio).toInt()
    }

    return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
}
