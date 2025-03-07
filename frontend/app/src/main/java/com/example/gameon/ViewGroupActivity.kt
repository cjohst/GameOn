package com.example.gameon

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.gameon.classes.Group
import com.example.gameon.ui.theme.*
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.example.gameon.api.methods.SessionDetails
import com.example.gameon.api.methods.fetchGroupUrl
import com.example.gameon.api.methods.getGroupMembers
import com.example.gameon.api.methods.logout
import com.example.gameon.classes.User
import com.example.gameon.composables.Header
import com.example.gameon.composables.ReportButton
import kotlinx.coroutines.launch

class ViewGroupActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val group: Group? = intent.getParcelableExtra("selected_group")
        val groupId = group?.group_id ?: 0
        val groupName = group?.group_name ?: "Unknown Group"
        val groupMembersState = mutableStateOf<List<User>>(emptyList())

        val user = SessionDetails(this).getUser()
        val discordId = user?.discord_id ?: "0"
        val discordUsername = user?.username ?: "Unknown"
        val discordAvatar = user?.avatar

        Log.d("ViewGroupActivity", "Group: $group")

        lifecycleScope.launch {
            val groupMemberList = getGroupMembers(groupId, this@ViewGroupActivity)
            val userList = groupMemberList.mapNotNull { it.user }
            groupMembersState.value = userList
        }

        Log.d("ViewGroupActivity", "Group Members: ${groupMembersState.value}")

        setContent {
            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = BlueDarker),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Header(
                    discordId,
                    discordUsername,
                    discordAvatar,
                    {
                        val intent = Intent(
                            this@ViewGroupActivity,
                            UserSettingsActivity::class.java
                        )
                        startActivity(intent)
                    },
                    {
                        lifecycleScope.launch {
                            logout(this@ViewGroupActivity)
                        }
                    }
                )
                Column(modifier = Modifier.weight(1f)) {
                    MainContent(groupMembersState, groupName, groupId)
                }
                ReportButton(
                    "Back",
                    outlined = true,
                    modifier = Modifier
                        .width(300.dp)
                        .padding(bottom = 80.dp)
                ) {
                    finish()
                }
            }
        }
    }
}

@Composable
fun GroupMembers(groupMembers: MutableState<List<User>>) {
    val fontFamily = FontFamily(Font(R.font.barlowcondensed_bold))

    val isLoading = groupMembers.value.isEmpty()
    val errorMessage = if (isLoading) "No members found" else null

    Box(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .height(200.dp)
            .clip(RoundedCornerShape(20.dp))
            .border(2.dp, Purple, RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Group Members",
                color = White,
                fontFamily = fontFamily,
                fontSize = 20.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                when {
                    isLoading -> Text(
                        text = "Loading...",
                        color = Purple,
                        fontFamily = fontFamily,
                        fontSize = 16.sp
                    )

                    errorMessage != null -> Text(
                        text = errorMessage,
                        color = Purple,
                        fontFamily = fontFamily,
                        fontSize = 16.sp
                    )

                    else -> LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(groupMembers.value) { member ->
                            val username = member.username

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .clip(RoundedCornerShape(15.dp))
                                    .background(Purple.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = username,
                                    color = White,
                                    fontFamily = fontFamily,
                                    fontSize = 16.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GoToDiscord(groupId: Int, context: Context) {
    val fontFamily = FontFamily(Font(R.font.barlowcondensed_bold))
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .height(70.dp)
            .clip(RoundedCornerShape(50.dp))
            .background(Purple)
            .clickable {
                coroutineScope.launch {
                    isLoading = true
                    val groupUrl = fetchGroupUrl(groupId, context)
                    isLoading = false

                    if (groupUrl != null) {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(groupUrl))
                        context.startActivity(intent)
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (isLoading) "Loading..." else "Go to Discord Group",
            color = BlueDarker,
            style = TextStyle(
                fontFamily = fontFamily,
                fontSize = 25.sp
            )
        )
    }
}

@Composable
fun MainContent(groupMembers: MutableState<List<User>>, groupName: String, groupId: Int) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = groupName,
            color = Blue,
            style = TextStyle(
                fontFamily = FontFamily(Font(R.font.barlowcondensed_bold)),
                fontSize = 30.sp,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        GroupMembers(groupMembers)

        Spacer(modifier = Modifier.height(20.dp))

        GoToDiscord(groupId, LocalContext.current)
    }
}