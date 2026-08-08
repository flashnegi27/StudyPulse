package com.studypulse.app.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.studypulse.app.domain.model.DailyStats
import java.time.DayOfWeek
import java.time.LocalDate

@Composable
fun DailyBarChart(
    dailyStats: List<DailyStats>,
    modifier: Modifier = Modifier
) {
    val primaryColor       = MaterialTheme.colorScheme.primary
    val secondaryColor     = MaterialTheme.colorScheme.secondary
    val surfaceVariant     = MaterialTheme.colorScheme.surfaceVariant
    val onSurfaceColor     = MaterialTheme.colorScheme.onSurface
    val textMeasurer       = rememberTextMeasurer()
    val today              = LocalDate.now().dayOfWeek

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(140.dp)
    ) {
        val barCount     = dailyStats.size
        val labelHeight  = 24.dp.toPx()
        val chartHeight  = size.height - labelHeight
        val barWidth     = size.width / (barCount * 2f)
        val maxSeconds   = dailyStats.maxOfOrNull { it.totalSeconds }?.takeIf { it > 0 } ?: 3600L
        val goalAvgSec   = 36_000L / 7.0f  // 10h / 7 days avg

        val dashEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f))

        val guidelineY = chartHeight - (goalAvgSec / maxSeconds * chartHeight).coerceIn(4f, chartHeight - 4f)
        drawLine(
            color       = onSurfaceColor.copy(alpha = 0.25f),
            start       = Offset(0f, guidelineY),
            end         = Offset(size.width, guidelineY),
            strokeWidth = 1.5f,
            pathEffect  = dashEffect
        )

        dailyStats.forEachIndexed { index, stats ->
            val x          = index * (size.width / barCount) + barWidth / 2
            val barHeight  = if (maxSeconds > 0) (stats.totalSeconds.toFloat() / maxSeconds * chartHeight) else 0f
            val barColor   = when {
                stats.dayOfWeek == today                -> primaryColor
                stats.dayOfWeek.value < today.value     -> secondaryColor
                else                                    -> surfaceVariant
            }

            drawRoundRect(
                color        = barColor,
                topLeft      = Offset(x, chartHeight - barHeight.coerceAtLeast(4f)),
                size         = Size(barWidth, barHeight.coerceAtLeast(4f)),
                cornerRadius = CornerRadius(4.dp.toPx())
            )

            drawDayLabel(
                textMeasurer = textMeasurer,
                day          = stats.dayOfWeek,
                x            = x + barWidth / 2,
                y            = chartHeight + 6.dp.toPx(),
                color        = onSurfaceColor,
                isToday      = stats.dayOfWeek == today
            )
        }
    }
}

private fun DrawScope.drawDayLabel(
    textMeasurer: TextMeasurer,
    day: DayOfWeek,
    x: Float,
    y: Float,
    color: androidx.compose.ui.graphics.Color,
    isToday: Boolean
) {
    val label = day.name.take(1)
    val style = TextStyle(
        fontSize   = 11.sp,
        color      = if (isToday) color else color.copy(alpha = 0.6f)
    )
    val measured = textMeasurer.measure(label, style)
    drawText(
        textLayoutResult = measured,
        topLeft          = Offset(x - measured.size.width / 2f, y)
    )
}
