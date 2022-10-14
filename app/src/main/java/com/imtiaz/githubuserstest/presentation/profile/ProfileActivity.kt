package com.imtiaz.githubuserstest.presentation.profile

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.imtiaz.githubuserstest.core.extensions.APP_THEME
import com.imtiaz.githubuserstest.core.extensions.CURRENT_USER
import com.imtiaz.githubuserstest.data.local.db.entity.GithubUser
import com.imtiaz.githubuserstest.presentation.main.MainViewModel
import com.imtiaz.githubuserstest.presentation.profile.ui.component.UserDetails
import com.imtiaz.githubuserstest.presentation.profile.ui.theme.GithubUsersTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileActivity : ComponentActivity() {

    private val viewModel: ProfileViewModel by viewModels()
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // fetching user info from db
        val user = getUsersFromIntent(intent)
        user?.let {
            viewModel.getUser(it.id, it.login)
        }

        setContent {
            GithubUsersTheme(
                darkTheme = getTheme(intent)
            ) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Scaffold(
                        topBar = {
                            TopBar(user?.login ?: "Github User App", this)
                        }
                    ) {
                        UserDetails(this)
                    }
                }
            }
        }
    }

}

@Composable
fun TopBar(userName: String, activity: Activity) {
    TopAppBar(
        title = { Text(text = userName) },
        navigationIcon = {
            IconButton(
                onClick = { activity.finish() }
            ) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back Icon")
            }
        }
    )
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    GithubUsersTheme {}
}

fun getUsersFromIntent(intent: Intent): GithubUser? {
    return if (Build.VERSION.SDK_INT >= 33) {
        intent.getParcelableExtra<GithubUser>(CURRENT_USER, GithubUser::class.java)
    } else {
        intent.getParcelableExtra<GithubUser>(CURRENT_USER)
    }
}

fun getTheme(intent: Intent): Boolean {
    return intent.getBooleanExtra(APP_THEME, false)
}


