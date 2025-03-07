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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.gameon.api.methods.SessionDetails
import com.example.gameon.api.methods.getReports
import com.example.gameon.api.methods.logout
import com.example.gameon.classes.Group
import com.example.gameon.classes.Report
import com.example.gameon.classes.User
import com.example.gameon.composables.Avatar
import com.example.gameon.composables.Header
import com.example.gameon.composables.Logo
import com.example.gameon.composables.ReportButton
import com.example.gameon.composables.ReportTitle
import com.example.gameon.ui.theme.*
import kotlinx.coroutines.launch

class ListReportsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val width = 300.dp

        val reportListState = mutableStateOf<List<Report>>(emptyList())
        val user = SessionDetails(this).getUser()
        val discordId = user?.discord_id ?: "0"
        val discordUsername = user?.username ?: "Unknown"
        val discordAvatar = user?.avatar

        lifecycleScope.launch{

            val reportList = getReports(
                unresolved = true,
                context = this@ListReportsActivity
            )
            if (reportList.isNotEmpty()) {
                reportListState.value = reportList
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
                            this@ListReportsActivity,
                            UserSettingsActivity::class.java
                        )
                        startActivity(intent)
                    },
                    {
                        lifecycleScope.launch {
                            logout(this@ListReportsActivity)
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
                    ) {
                        ReportTitle(
                            "List Reports",
                            Modifier.width(width)
                        )
                        ReportList(
                            reportListState.value,
                            Modifier
                                .width(width)
                                .height(550.dp)
                        ) {
                                reportId ->
                            startActivity(Intent(
                                this@ListReportsActivity,
                                ViewReportsActivity::class.java
                            ).apply { putExtra("ReportId", reportId) })
                            finish()
                        }
                        ReportButton(
                            "Back",
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
fun ReportList(
    reportList: List<Report>,
    modifier: Modifier,
    onClick: (reportId: Int) -> Unit
) {
    Column(
        modifier,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        reportList.forEach { report ->
            ReportListItem(
                report.report_id!!,
                report.reported_user!!.username,
                { Avatar(
                    report.reported_user.discord_id,
                    report.reported_user.avatar,
                ) }
            ) {
                onClick(report.report_id)
            }
        }
        if (reportList.isEmpty())
            Text(
                "There are no reports to review.",
                color = White
            )
    }
}

@Composable
fun ReportListItem(
    reportId: Int,
    username: String,
    icon: @Composable (()->Unit)? = null,
    onClick: () -> Unit = {}
) {
    Button(
        contentPadding = PaddingValues(0.dp),
        shape = RectangleShape,
        onClick = onClick
    ) {
        ListItem(
            headlineContent = { Text("Report $reportId") },
            leadingContent = icon,
            trailingContent = { Text(username) },
            colors = ListItemDefaults.colors(
                containerColor = BlueDark,
                headlineColor = White,
                trailingIconColor = White
            )
        )
    }

}

@Preview(showBackground = true)
@Composable
fun ListReportsPreview() {

    val exampleReport = Report(
        report_id = 1,
        reporter = User(
            discord_id = "1",
            username = "sanhal23",
            email = "sanhal23@discord.com",
            banned = false
        ),
        reported_user = User(
            discord_id = "2",
            username = "rubination",
            email = "rubination@discord.com",
            banned = false
        ),
        group = Group(
            group_id = 1,
            group_name = "Sims Swappers",
            max_players = 3,
            game_id = 1
        ),
        reason = "This person keeps saying that I stole his diamonds but consistently " +
                "steals other people's netherite."
    )

    val exampleReportList = List(5) {exampleReport}

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
        ) {
            ReportTitle(
                "List Reports",
                Modifier.width(width)
            )
            ReportList(
                exampleReportList,
                Modifier
                    .width(width)
                    .height(550.dp)
            ) { }
            ReportButton(
                "Back",
                outlined = true,
                modifier = Modifier.width(width)
            )
        }
    }
}