package com.studypulse.app.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.studypulse.app.domain.model.StudySession

@Composable
fun SessionListItem(
    session: StudySession,
    modifier: Modifier = Modifier
) {
    ListItem(
        modifier     = modifier,
        headlineContent = {
            Text(
                text  = session.locationId,
                style = MaterialTheme.typography.titleMedium
            )
        },
        supportingContent = {
            Column {
                Text(
                    text  = session.date,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        },
        leadingContent = {
            Icon(
                imageVector        = Icons.Filled.LocationOn,
                contentDescription = null,
                tint               = MaterialTheme.colorScheme.primary
            )
        },
        trailingContent = {
            Text(
                text  = session.formattedDuration(),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    )
}
