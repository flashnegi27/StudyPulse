package com.studypulse.app.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.studypulse.app.R

@Composable
fun WeeklyProgressRing(
    fraction: Float,
    totalHours: Float,
    modifier: Modifier = Modifier
) {
    val progressColor = MaterialTheme.colorScheme.primary
    val trackColor    = MaterialTheme.colorScheme.surfaceVariant

    Box(
        contentAlignment = Alignment.Center,
        modifier         = modifier.size(180.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke    = Stroke(width = 18.dp.toPx(), cap = StrokeCap.Round)
            val sweepAngle = fraction * 360f

            drawArc(
                color      = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter  = false,
                style      = stroke
            )
            if (sweepAngle > 0f) {
                drawArc(
                    color      = progressColor,
                    startAngle = -90f,
                    sweepAngle = sweepAngle,
                    useCenter  = false,
                    style      = stroke
                )
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text  = "%.1f".format(totalHours),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text  = stringResource(R.string.weekly_goal_hrs),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}
