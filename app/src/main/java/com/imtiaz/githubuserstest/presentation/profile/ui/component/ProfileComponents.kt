package com.imtiaz.githubuserstest.presentation.profile.ui.component

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imtiaz.githubuserstest.R
import com.imtiaz.githubuserstest.core.extensions.loadImage
import com.imtiaz.githubuserstest.presentation.profile.ui.theme.Purple500

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

@Composable
fun UserDetails() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxWidth()
    ) {
        Column {
            Spacer(modifier = Modifier.height(40.dp))
            UserInfoCard()
        }
        UserImage()
    }
}

@Composable
fun UserInfoCard() {
    Card(
        backgroundColor = Color.White,
        modifier = Modifier
            .padding(30.dp)
            .fillMaxWidth()
            .height(250.dp)
            .shadow(
                elevation = 5.dp,
                shape = RoundedCornerShape(10.dp)
            )
    ) {
        Column {
            Spacer(modifier = Modifier.height(70.dp))
            Text(
                text = "Imtiaz Dipto",
                textAlign = TextAlign.Center,
                color = Purple500,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_loc),
                    contentDescription = "location Icon",
                    modifier = Modifier
                        .width(20.dp)
                        .height(20.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = "Dhaka, Bangladesh",
                    textAlign = TextAlign.Center,
                    color = Color.DarkGray,
                    fontWeight = FontWeight.W200,
                    fontSize = 14.sp,
                )
            }
        }
    }
}

@Composable
fun UserImage() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Card(
            backgroundColor = Color.White,
            modifier = Modifier
                .padding(25.dp)
                .width(100.dp)
                .height(100.dp)
                .shadow(
                    elevation = 5.dp,
                    shape = RoundedCornerShape(50.dp)
                )
        ) {
            val image = loadImage(url = "https://avatars.githubusercontent.com/u/11765327?v=4")
            image.value?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "location Icon",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun UserMoreInfo() {

}