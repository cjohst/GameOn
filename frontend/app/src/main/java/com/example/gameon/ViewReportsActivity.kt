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
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.gameon.api.methods.getReportById
import com.example.gameon.api.methods.resolveReport
import com.example.gameon.classes.User
import com.example.gameon.composables.Avatar
import com.example.gameon.composables.Logo
import com.example.gameon.composables.ReportButton
import com.example.gameon.composables.ReportTitle
import com.example.gameon.ui.theme.*
import kotlinx.coroutines.launch

class ViewReportsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val reportId = intent.getIntExtra("ReportId", 0)
        val width = 300.dp

        val groupName = mutableStateOf("")
        val reporter = mutableStateOf<User?>(null)
        val reportedUser = mutableStateOf<User?>(null)
        val reason = mutableStateOf("")

        lifecycleScope.launch {
            val report = getReportById(reportId, this@ViewReportsActivity)!!

            groupName.value = report.group!!.group_name
            reporter.value = report.reporter
            reportedUser.value = report.reported_user
            reason.value = report.reason
        }

        setContent {
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
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ReportTitle(
                        "Report $reportId",
                        Modifier.width(width)
                    )
                    ReportDetails(
                        groupName.value,
                        reporter.value,
                        reportedUser.value,
                        reason.value,
                        Modifier.width(width)
                    )
                    ReportButton(
                        "Ban User",
                        containerColor = Red,
                        modifier = Modifier.width(width)
                    ) {
                        lifecycleScope.launch {
                            resolveReport(reportId, true, this@ViewReportsActivity)
                        }
                    }
                    ReportButton(
                        "Acquit User",
                        containerColor = Blue,
                        modifier = Modifier.width(width)
                    ) {
                        lifecycleScope.launch {
                            resolveReport(reportId, false, this@ViewReportsActivity)
                        }
                    }
                    ReportButton(
                        "Cancel",
                        outlined = true,
                        modifier = Modifier.width(width)
                    ) {
                        startActivity(Intent(
                            this@ViewReportsActivity,
                            ListReportsActivity::class.java
                        ))
                        finish()
                    }
                }
            }
        }
    }
}

@Composable
fun ReportDetails(
    groupName: String,
    reporter: User?,
    reportedUser: User?,
    reason: String,
    modifier: Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(25.dp, Alignment.CenterVertically),
    ) {
        InputReadable(
            groupName,"Group", modifier = modifier,
        )
        if (reporter != null)
            InputReadable(
                reporter.username,
                "Reporter",
                { Avatar(
                    reporter.discord_id,
                    reporter.avatar,
                ) },
                modifier
            )
        if (reportedUser != null)
            InputReadable(
                reportedUser.username,
                "Reported User",
                { Avatar(
                    reportedUser.discord_id,
                    reportedUser.avatar,
                ) },
                modifier
            )
        InputReadable(
            reason, "Reason", modifier = modifier.height(200.dp)
        )
    }
}

@Composable
fun InputReadable(
    input: String,
    label: String,
    icon: @Composable (() -> Unit)? = null,
    modifier: Modifier
) {
    val fontFamily = FontFamily(Font(R.font.lato_regular))

    TextField(
        enabled = false,
        value = input,
        onValueChange = {},
        label = { Text(
            label,
            fontFamily=fontFamily
        ) },
        leadingIcon = icon,
        singleLine = false,
        textStyle = TextStyle(fontFamily = fontFamily),
        colors = TextFieldDefaults.colors(
            disabledTextColor = White,
            disabledLabelColor = White,
            disabledContainerColor = BlueDark,
            disabledIndicatorColor = BlueDark,
        ),
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun ViewReportsPreview() {
    val width = 300.dp
    val reportId = 1

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
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ReportTitle(
                "Report $reportId",
                Modifier.width(width)
            )
            ReportDetails(
                "Sims Swapper",
                User("1", "sanhal23", "email@dc.com", banned=false),
                User("2", "rubination", "email@dc.com", banned=false),
                "This person wasn't very nice to me.",
                Modifier.width(width)
            )
            ReportButton(
                "Ban User",
                containerColor = Red,
                modifier = Modifier.width(width)
            )
            ReportButton(
                "Acquit User",
                containerColor = Blue,
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