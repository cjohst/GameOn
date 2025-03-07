package com.example.gameon

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.gameon.api.methods.SessionDetails
import com.example.gameon.api.methods.getGroupMembers
import com.example.gameon.api.methods.getUserGroups
import com.example.gameon.api.methods.logout
import com.example.gameon.api.methods.submitReport
import com.example.gameon.classes.Group
import com.example.gameon.classes.Report
import com.example.gameon.classes.User
import com.example.gameon.composables.Avatar
import com.example.gameon.composables.DropdownInput
import com.example.gameon.composables.Header
import com.example.gameon.composables.Logo
import com.example.gameon.composables.ReportButton
import com.example.gameon.composables.ReportTitle
import com.example.gameon.composables.TextInput
import com.example.gameon.ui.theme.*
import kotlinx.coroutines.launch

class ReportsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val groupListState = mutableStateOf<List<Group>>(emptyList())
        val userListState = mutableStateOf<List<User>>(emptyList())

        val selectedGroup = mutableStateOf<Group?>(null)
        val selectedUser = mutableStateOf<User?>(null)
        val reason = mutableStateOf("")

        val user = SessionDetails(this).getUser()
        val discordId = user?.discord_id ?: "0"
        val discordUsername = user?.username ?: "Unknown"
        val discordAvatar = user?.avatar

        val canSubmit = mutableStateOf(false)

        val width = 300.dp

        lifecycleScope.launch{
            val groupList = getUserGroups(
                context = this@ReportsActivity
            )
            if (groupList.isNotEmpty()) {
                groupListState.value = groupList
            }
        }

        setContent {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = BlueDarker),
                verticalArrangement = Arrangement.Top, // Keep everything stacked from the top
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Header(
                    discordId,
                    discordUsername,
                    discordAvatar,
                    {
                        val intent = Intent(
                            this@ReportsActivity,
                            UserSettingsActivity::class.java
                        )
                        startActivity(intent)
                    },
                    {
                        lifecycleScope.launch {
                            logout(this@ReportsActivity)
                        }
                    }
                )
                Box (
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = BlueDarker)
                ) {
                    Box(
                        modifier = Modifier
                            .align(alignment = Alignment.TopStart)
                            .offset(15.dp, 30.dp)
                    )
                    Column (
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ){
                        ReportTitle(
                            "Report User",
                            Modifier.width(width)
                        )
                        Reports(
                            groupListState.value,
                            selectedGroup,
                            userListState.value,
                            selectedUser,
                            reason,
                            canSubmit,
                            Modifier.width(width)
                        ) {
                            lifecycleScope.launch {
                                // Default to 0 if not found
                                val groupId = selectedGroup.value?.group_id ?: 0

                                val groupMemberList =
                                    getGroupMembers(groupId, this@ReportsActivity)

                                val userList = groupMemberList.map { it.user!! }

                                userListState.value = userList
                            }
                        }
                        ReportButton(
                            "Submit Report",
                            containerColor = Red,
                            enabled = canSubmit.value,
                            modifier = Modifier.width(width)
                        ) {
                            lifecycleScope.launch {
                                // Default to 0 if not found
                                val groupId = selectedGroup.value?.group_id ?: 0

                                // Default to "0" if not found
                                val reportedDiscordId = selectedUser.value?.discord_id ?: "0"

                                submitReport(
                                    Report(
                                        group_id = groupId,
                                        reported_discord_id = reportedDiscordId,
                                        reason = reason.value,
                                    ),
                                    context = this@ReportsActivity,
                                )
                            }
                        }
                        ReportButton(
                            "Cancel",
                            outlined = true,
                            modifier = Modifier.width(width)
                        ) {
                            finish()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Reports(
    groupList: List<Group>,
    selectedGroup: MutableState<Group?>,
    userList: List<User>,
    selectedUser: MutableState<User?>,
    reason: MutableState<String>,
    canSubmit: MutableState<Boolean>,
    modifier: Modifier,
    onSelectedGroup: () -> Unit = { },
) {
    canSubmit.value = selectedGroup.value != null &&
            selectedUser.value != null &&
            reason.value.isNotBlank()

    Column (
        verticalArrangement = Arrangement.spacedBy(25.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        DropdownInput(
            "Group",
            groupList,
            selectedGroup,
            modifier = modifier,
            displayText = { it.group_name },
            onSelect = onSelectedGroup,
        )
        if (groupList.isNotEmpty() && selectedGroup.value != null)
            DropdownInput(
                "User",
                userList,
                selectedUser,
                modifier = modifier,
                displayText = { it.username },
                leadingIcon = {{ Avatar(it.discord_id, it.avatar) }}
            )
        TextInput(
            reason,
            modifier = modifier.height(350.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ReportsPreview() {
    val groupList = listOf(
        Group(1, "Sims Swappers", game_id=1, max_players=3)
    )
    val userList = listOf(
        User("1", "caboose4020", email="email@dc.com")
    )

    val selectedGroup = remember { mutableStateOf<Group?>(groupList[0]) }
    val selectedUser = remember { mutableStateOf<User?>(userList[0]) }
    val reason = remember { mutableStateOf(
        "This person called me many bad words while playing The Sims."
    ) }

    val width = 300.dp

    Box (
        modifier = Modifier
            .fillMaxSize()
            .background(color = BlueDarker)
    ) {
        Box(
            modifier = Modifier
                .align(alignment = Alignment.TopStart)
                .offset(15.dp, 30.dp)
        ) { Logo() }
        Column (
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            ReportTitle(
                "Report User",
                Modifier.width(width)
            )
            Reports(
                groupList,
                selectedGroup,
                userList,
                selectedUser,
                reason,
                remember { mutableStateOf(true) },
                Modifier.width(width)
            )
            ReportButton(
                "Report User",
                containerColor = Red,
                modifier = Modifier.width(width)
            )
            ReportButton(
                "Cancel",
                outlined = true,
                modifier = Modifier.width(width)
            )
        }
    }

}