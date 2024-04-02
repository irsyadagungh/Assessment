package org.d3if0065.assessment.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import org.d3if0065.assessment.R
import org.d3if0065.assessment.navigation.ScreenManager

@Composable
fun Welcome(navController: NavHostController) {
    Surface(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier
                .padding(top = 64.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)

        ) {
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp),
                painter = painterResource(R.drawable.welcome),
                contentDescription = stringResource(R.string.welcome)
            )

            Spacer(modifier = Modifier.height(100.dp))

            Text(
                text = stringResource(R.string.welcome),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineLarge
            )

            Spacer(modifier = Modifier.height(80.dp))

            Text(
                text = stringResource(R.string.welcome_app),
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(150.dp))

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { navController.navigate(ScreenManager.Home.route) }
            ) {
                Text(text = stringResource(R.string.next))
            }
        }
    }
}