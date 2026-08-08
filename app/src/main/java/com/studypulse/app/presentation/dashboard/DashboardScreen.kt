package com.studypulse.app.presentation.dashboard

import android.Manifest
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.studypulse.app.R
import com.studypulse.app.domain.model.SessionState
import com.studypulse.app.presentation.components.DailyBarChart
import com.studypulse.app.presentation.components.SessionListItem
import com.studypulse.app.presentation.components.WeeklyProgressRing

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onScannerClick: () -> Unit,
    onTapTriggered: (String) -> Unit,
    innerPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    val sessionState   by viewModel.sessionState.collectAsStateWithLifecycle()
    val elapsedSeconds by viewModel.elapsedSeconds.collectAsStateWithLifecycle()
    val weeklyStats    by viewModel.weeklyStats.collectAsStateWithLifecycle()
    val recentSessions by viewModel.recentSessions.collectAsStateWithLifecycle()

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val notifPermission = rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
        LaunchedEffect(Unit) {
            if (!notifPermission.status.isGranted) {
                notifPermission.launchPermissionRequest()
            }
        }
    }

    LazyColumn(
        modifier      = modifier
            .fillMaxSize()
            .padding(innerPadding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            StatusCard(
                sessionState   = sessionState,
                elapsedSeconds = elapsedSeconds,
                onTapTriggered = onTapTriggered,
                onScannerClick = onScannerClick
            )
        }

        item {
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier            = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text  = stringResource(R.string.weekly_progress),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(Modifier.height(12.dp))
                    WeeklyProgressRing(
                        fraction   = weeklyStats.progressFraction,
                        totalHours = weeklyStats.totalHours
                    )
                }
            }
        }

        item {
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text  = stringResource(R.string.daily_breakdown),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(Modifier.height(12.dp))
                    DailyBarChart(dailyStats = weeklyStats.dailyStats)
                }
            }
        }

        if (recentSessions.isNotEmpty()) {
            item {
                Text(
                    text  = stringResource(R.string.recent_activity),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            items(recentSessions, key = { it.id }) { session ->
                Card(
                    modifier  = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    SessionListItem(session = session)
                }
            }
        } else {
            item {
                Text(
                    text     = stringResource(R.string.no_sessions),
                    style    = MaterialTheme.typography.bodyMedium,
                    color    = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun StatusCard(
    sessionState: SessionState,
    elapsedSeconds: Long,
    onTapTriggered: (String) -> Unit,
    onScannerClick: () -> Unit
) {
    val isStudying = sessionState is SessionState.Studying
    val cardColor  = if (isStudying)
        MaterialTheme.colorScheme.primaryContainer
    else
        MaterialTheme.colorScheme.surfaceVariant

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors   = CardDefaults.elevatedCardColors(containerColor = cardColor)
    ) {
        Column(
            modifier            = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isStudying) {
                val state = sessionState as SessionState.Studying
                Text(
                    text  = "Studying · ${state.locationId}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text      = formatElapsed(elapsedSeconds),
                    style     = MaterialTheme.typography.displayLarge.copy(
                        fontFamily = FontFamily.Monospace
                    ),
                    color     = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(Modifier.height(16.dp))
                FilledTonalButton(onClick = { onTapTriggered(state.locationId) }) {
                    Icon(Icons.Filled.Stop, contentDescription = null)
                    Text(stringResource(R.string.stop_session), modifier = Modifier.padding(start = 8.dp))
                }
            } else {
                Icon(
                    imageVector        = Icons.Filled.Nfc,
                    contentDescription = null,
                    tint               = MaterialTheme.colorScheme.primary,
                    modifier           = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text  = stringResource(R.string.not_studying),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text  = "Tap NFC tag or scan QR to start",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Spacer(Modifier.height(16.dp))
                FilledTonalButton(onClick = onScannerClick) {
                    Icon(Icons.Filled.PlayArrow, contentDescription = null)
                    Text(stringResource(R.string.scan_qr), modifier = Modifier.padding(start = 6.dp))
                }
            }
        }
    }
}

private fun formatElapsed(seconds: Long): String {
    val h = seconds / 3600
    val m = (seconds % 3600) / 60
    val s = seconds % 60
    return "%02d:%02d:%02d".format(h, m, s)
}
