package com.example.gameon.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter

@Composable
fun Avatar(discordId: String?, avatarId: String?, modifier: Modifier = Modifier, size: Dp = 40.dp) {
    val avatarLink = if(discordId != "Unknown" && avatarId != null) {
        "https://cdn.discordapp.com/avatars/$discordId/$avatarId.png?size=256"
    } else {
        val defaultAvatarIndex = if (discordId != null && discordId != "Unknown")
            (discordId.toLong() shr 22) % 6
        else 0L
        "https://cdn.discordapp.com/embed/avatars/$defaultAvatarIndex.png"
    }

    Box(modifier = Modifier.size(size)) {
        Image(
            rememberAsyncImagePainter(avatarLink),
            "Discord Avatar",
            contentScale = ContentScale.FillBounds,
            modifier = modifier.clip(CircleShape).fillMaxSize()
        )
    }
}