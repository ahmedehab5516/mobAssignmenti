package com.ahmed.cinema.presentation.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextOverflow
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarWithUser(
    title: String,
    onSettingsClick: (() -> Unit)? = null,
    showSettingsIcon: Boolean = false
) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userName = currentUser?.displayName
        ?: currentUser?.email?.substringBefore("@")
        ?: "Guest"

    TopAppBar(
        title = {
            Text(
                text = userName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
//        actions = {
//            if (showSettingsIcon && onSettingsClick != null) {
//                IconButton(onClick = onSettingsClick) {
//                    Icon(Icons.Default.Settings, contentDescription = "Settings")
//                }
//            }
//        }
    )
}
