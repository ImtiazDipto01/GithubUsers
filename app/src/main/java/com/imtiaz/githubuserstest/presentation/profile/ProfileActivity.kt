package com.imtiaz.githubuserstest.presentation.profile

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.imtiaz.githubuserstest.core.extensions.CURRENT_USER
import com.imtiaz.githubuserstest.data.local.db.entity.GithubUser
import com.imtiaz.githubuserstest.presentation.profile.ui.component.TopBar
import com.imtiaz.githubuserstest.presentation.profile.ui.component.UserDetails
import com.imtiaz.githubuserstest.presentation.profile.ui.theme.GithubUsersTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GithubUsersTheme {
                // A surface container using the 'background' color from the theme
                val user = getUsersFromIntent(intent)
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Scaffold(
                        topBar = {
                            TopBar(user?.login ?: "Github User App", this)
                        }
                    ) {
                        UserDetails()
                    }
                }
            }
        }
    }
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

