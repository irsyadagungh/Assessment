package org.d3if0065.assessment.ui.screen

import android.content.Context
import android.content.Intent
import android.content.LocusId
import android.service.controls.actions.FloatAction
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.ClearCredentialException
import androidx.credentials.exceptions.GetCredentialException
import androidx.datastore.dataStore
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.d3if0065.assessment.BuildConfig
import org.d3if0065.assessment.R
import org.d3if0065.assessment.component.DisplayAlertDialog
import org.d3if0065.assessment.database.ArticleDb
import org.d3if0065.assessment.model.Article
import org.d3if0065.assessment.model.User
import org.d3if0065.assessment.navigation.ScreenManager
import org.d3if0065.assessment.network.ApiStatus
import org.d3if0065.assessment.network.ArticleApi
import org.d3if0065.assessment.network.UserDataStore
import org.d3if0065.assessment.util.SettingsDataStore
import org.d3if0065.assessment.util.ViewModelFactory
import org.d3if0065.assessment.viewModel.DetailViewModel
import org.d3if0065.assessment.viewModel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(navController: NavHostController = rememberNavController()) {
    val context = LocalContext.current
    val dataStoreUser = UserDataStore(context)
    val user by dataStoreUser.userFlow.collectAsState(User())

    var showDialog by remember { mutableStateOf(false) }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val dataStore = SettingsDataStore(LocalContext.current)
    val showList by dataStore.layoutFlow.collectAsState(true)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        dataStore.saveLayout(!showList)
                    }
                }) {
                    Icon(
                        painter = if (showList) painterResource(R.drawable.baseline_grid_view_24) else painterResource(R.drawable.baseline_view_list_24),
                        contentDescription = "List",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                } },
                title = {
                    Text(
                        text = stringResource(R.string.article),
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                },
                actions = {
                    IconButton(onClick = {
                        if (user.email.isEmpty()){
                            CoroutineScope(Dispatchers.IO).launch {
                                signIn(context, dataStoreUser)
                            }
                        } else {
                            showDialog = true
                        }
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.account),
                            contentDescription = stringResource(R.string.profile),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
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
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (user.email.isEmpty()){
                        CoroutineScope(Dispatchers.IO).launch {
                            signIn(context, dataStoreUser)
                        }
                    } else {
                        navController.navigate(ScreenManager.FormArticle.route)
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(R.string.add)
                )
            }
        }

    ) { padding ->
        HomeContent(modifier = Modifier.padding(padding), navController, showList, user.email)
        if (showDialog) {
            ProfileDialog(user = user, onDismissRequest = { showDialog = false }) {
                CoroutineScope(Dispatchers.IO).launch {
                    signOut(context, dataStoreUser)
                }
                showDialog = false
                
            }
        }
    }
}

@Composable
fun HomeContent(modifier: Modifier, navController: NavHostController, showList: Boolean, userId: String) {

    val context = LocalContext.current
    val db = ArticleDb.getInstance(context)
    val factory = ViewModelFactory(db.dao)
    val viewModel: MainViewModel = viewModel()
    val data by viewModel.data
    val status by viewModel.status.collectAsState()

    LaunchedEffect(userId) {
        viewModel.retrieveData(userId)
    }

//    val viewDetailModel: DetailViewModel = viewModel(factory = factory)
//    val data by viewModel.data.collectAsState()


    if (data.isNotEmpty() || status == ApiStatus.LOADING) {
        when(status){
            ApiStatus.LOADING ->{
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ){
                    CircularProgressIndicator()
                }
            }
            ApiStatus.SUCCESS -> {
                if (showList) {
                    LazyColumn(
                        modifier = modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentPadding = PaddingValues(bottom = 84.dp)
                    ) {
                        items(data) { item ->
                            if (item.idUser == userId) {
                                ListItem(
                                    title = item.title,
                                    content = item.content,
                                    category = item.category,
                                    onClick = {
                                        navController.navigate(ScreenManager.EditArticle.withId(item.id))
                                    },
                                    viewModel = viewModel,
                                    id = item.id,
                                    navController = navController,
                                    idUser = item.idUser,
                                    userId = userId,
                                    image = item.image
                                )
                            }
                        }
                    }
                } else {
                    LazyVerticalStaggeredGrid(
                        modifier = modifier.fillMaxSize(),
                        columns = StaggeredGridCells.Fixed(2),
                        verticalItemSpacing = 8.dp,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(8.dp, 8.dp, 8.dp, 84.dp)
                    ) {
                        items(data) { item ->
                            if (item.idUser == userId) {
                                GridItem(
                                    id = item.id,
                                    title = item.title,
                                    content = item.content,
                                    category = item.category,
                                    idUser = item.idUser,
                                    userId = userId,
                                    image = item.image,
                                    viewModel = viewModel,
                                    onClick = {
                                        navController.navigate(ScreenManager.EditArticle.withId(item.id))
                                    }
                                )
                            }
                        }
                    }
                }
                if (data.isEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(R.drawable.no_data_amico),
                            contentDescription = stringResource(R.string.empty)
                        )
                        Text(
                            text = stringResource(R.string.empty),
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            ApiStatus.FAILED -> {
                Column (
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Text(text = "Error")
                    Button(
                        onClick = { viewModel.retrieveData(userId) },
                        modifier = Modifier.padding(top = 16.dp),
                        contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp)
                    ) {
                        Text(text = "Try Again")
                    }
                }
            }
        }
    }
}

@Composable
fun ListItem(
        title: String,
        content: String,
        category: String,
        idUser: String,
        userId: String,
        image: String,
        onClick: () -> Unit,
        viewModel: MainViewModel,
        id: Long,
        navController: NavHostController
    ) {

        val context = LocalContext.current
        var showDialog by remember { mutableStateOf(false) }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primaryContainer),
            elevation = CardDefaults.cardElevation(0.2.dp)
        ) {
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
            ){
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(ArticleApi.getArticleUrl(image))
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = R.drawable.loading_img),
                    error = painterResource(id = R.drawable.baseline_broken_image_24),
                    modifier = Modifier
                        .padding(16.dp)
                        .scale(2.0f)
                        .width(50.dp)
                        .height(50.dp)
                )
                Column (
                    modifier = Modifier.width(200.dp).padding(4.dp)
                ){
                    Text(
                        modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
                        text = title,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        modifier = Modifier.padding(16.dp),
                        text = content,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 2
                    )
                }
            }

            Divider(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = category,
                    textAlign = TextAlign.Start,
                    color = MaterialTheme.colorScheme.onPrimary,

                    modifier = Modifier
                        .padding(16.dp)
                        .background(
                            color = getColorByCategory(category = category),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                )

                Row(
                    horizontalArrangement = Arrangement.End
                ) {
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
                    if (idUser == userId) {
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
                                openDialog = showDialog,
                                onDismissRequest = { showDialog = !showDialog }
                            ) {
                                showDialog = false
                                CoroutineScope(Dispatchers.IO).launch {
                                    viewModel.deleteData(id, userId)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

@Composable
fun GridItem(
        id: Long,
        title: String,
        content: String,
        category: String,
        idUser: String,
        onClick: () -> Unit,
        userId: String,
        image: String,
        viewModel: MainViewModel
    ) {

    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .clickable { onClick() },
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primaryContainer),
            elevation = CardDefaults.cardElevation(0.2.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(ArticleApi.getArticleUrl(image))
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = R.drawable.loading_img),
                    error = painterResource(id = R.drawable.baseline_broken_image_24),
                    modifier = Modifier
                        .padding(4.dp)
                        .scale(2.0f)
                        .width(50.dp)
                        .height(50.dp)
                )
            }
            Text(
                modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
                text = title,
                fontWeight = FontWeight.Bold
            )
            Text(
                modifier = Modifier.padding(16.dp),
                text = content,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )

            Divider(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = category,
                    textAlign = TextAlign.Start,
                    color = MaterialTheme.colorScheme.onPrimary,

                    modifier = Modifier
                        .padding(8.dp)
                        .background(
                            color = getColorByCategory(category = category),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                )

                Row(
                    horizontalArrangement = Arrangement.End
                ) {
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
                    if (idUser == userId) {
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
                                openDialog = showDialog,
                                onDismissRequest = { showDialog = !showDialog }
                            ) {
                                showDialog = false
                                CoroutineScope(Dispatchers.IO).launch {
                                    viewModel.deleteData(id, userId)
                                }
                            }
                        }
                    }
                }
            }
        }

    }

private suspend fun signIn(context: Context, dataStore: UserDataStore) {
    val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId(BuildConfig.API_KEY)
        .build()

    val request: GetCredentialRequest = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

    try {
        val credentialManager = CredentialManager.create(context)
        val result = credentialManager.getCredential(context, request)
        handleSignIn(result, dataStore)
    } catch (e: GetCredentialException){
        Log.e("SIGN-IN", "Error: ${e.errorMessage}")
    }
}

private suspend fun handleSignIn(result: GetCredentialResponse, dataStore: UserDataStore) {
    val credential = result.credential
    if (credential is CustomCredential &&
        credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL){
        try {
            val googleId = GoogleIdTokenCredential.createFrom(credential.data)
            Log.d("SIGN-IN", "User email: ${googleId.id}")
            val nama = googleId.displayName ?: ""
            val email = googleId.id
            val photoUrl = googleId.profilePictureUri.toString()
            dataStore.saveData(User(name = nama, email = email, photoUrl = photoUrl))
        } catch (e: GoogleIdTokenParsingException){
            Log.e("SIGN-IN", "Error: ${e.message}")
        }
    } else {
        Log.e("SIGN-IN", "Error: Invalid credential")

    }
}

private suspend fun signOut(context: Context, dataStore: UserDataStore){
    try {
        val credentialManager = CredentialManager.create(context)
        credentialManager.clearCredentialState(ClearCredentialStateRequest())
        dataStore.saveData(User())
    } catch (e: ClearCredentialException){
        Log.e("SIGN-IN", "Error: ${e.errorMessage}")
    }
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
