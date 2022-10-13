package com.imtiaz.githubuserstest.presentation.profile.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.imtiaz.githubuserstest.R
import com.imtiaz.githubuserstest.core.extensions.loadImage
import com.imtiaz.githubuserstest.presentation.profile.ProfileViewModel
import com.imtiaz.githubuserstest.presentation.profile.ui.theme.Purple500


@Composable
fun UserDetails() {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            UserInfoCard()
            NoteInfoView("Enter your note...")
            Spacer(modifier = Modifier.fillMaxHeight(0.7f))
            SubmitButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(horizontal = 30.dp)
            )
        }
        UserImage()
    }
}

@Composable
fun UserInfoCard(viewModel: ProfileViewModel = hiltViewModel()) {
    val user = viewModel.profileState.user.value
    val isLoading = viewModel.profileState.isLoading.value
    Card(
        backgroundColor = Color.White,
        modifier = Modifier
            .padding(30.dp)
            .fillMaxWidth()
            .shadow(
                elevation = 5.dp,
                shape = RoundedCornerShape(10.dp)
            )
    ) {
        Column {
            Spacer(modifier = Modifier.height(70.dp))
            Text(
                text = user?.name ?: "",
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
                    text = user?.location ?: "Not Found",
                    textAlign = TextAlign.Center,
                    color = Color.DarkGray,
                    fontWeight = FontWeight.W200,
                    fontSize = 14.sp,
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.fillMaxWidth()
            ) {
                UserMoreInfo("Followers")
                UserMoreInfo("Repository")
                UserMoreInfo("Following")
            }
            Spacer(modifier = Modifier.height(26.dp))
            if (isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                )
            }
        }
    }
}

@Composable
fun UserImage(
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val userImg = viewModel.profileState.user.value?.avatarUrl
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
            val image = loadImage(url = userImg)
            image.value?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "location Icon",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Composable
fun UserMoreInfo(sectionName: String, viewModel: ProfileViewModel = hiltViewModel()) {
    val count = when (sectionName) {
        "Followers" -> viewModel.profileState.user.value?.followers
        "Repository" -> viewModel.profileState.user.value?.publicRepos
        else -> viewModel.profileState.user.value?.following
    }
    Column(
        modifier = Modifier.padding(10.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = count.toString(),
            textAlign = TextAlign.Center,
            color = Color.Black,
            fontWeight = FontWeight.W400,
            fontSize = 17.sp,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = sectionName,
            textAlign = TextAlign.Center,
            color = Color.Gray,
            fontWeight = FontWeight.W400,
            fontSize = 16.sp,
        )

    }
}

@Composable
fun NoteInfoView(
    hint: String = "",
    viewModel: ProfileViewModel = hiltViewModel()
) {

    var text by remember {
        mutableStateOf(viewModel.profileState.user.value?.note ?: "")
    }
    var isHintDisplayed by remember {
        mutableStateOf(hint.isNotEmpty())
    }

    Box() {
        Card(
            backgroundColor = Color.White,
            modifier = Modifier
                .padding(horizontal = 30.dp)
                .fillMaxWidth()
                .height(100.dp)
                .shadow(
                    elevation = 5.dp,
                    shape = RoundedCornerShape(10.dp)
                )
        ) {
            BasicTextField(
                value = text,
                onValueChange = { text = it },
                maxLines = 3,
                textStyle = TextStyle(
                    color = Color.DarkGray,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Start
                ),
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White, RoundedCornerShape(10.dp))
                    .padding(16.dp)
                    .onFocusChanged {
                        isHintDisplayed = !it.isFocused
                    },
            )
        }

        if (isHintDisplayed) {
            Text(
                text = hint,
                textAlign = TextAlign.Start,
                color = Color.Gray,
                fontSize = 15.sp,
                modifier = Modifier.padding(horizontal = 46.dp, vertical = 16.dp)
            )
        }
    }
}

@Composable
fun SubmitButton(modifier: Modifier) {
    Button(
        onClick = { /*TODO*/ },
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Purple500,
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(percent = 50),
    ) {
        Text(
            text = "Save",
            modifier = Modifier.padding(horizontal = 40.dp, vertical = 4.dp),
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}