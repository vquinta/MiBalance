package com.example.mibalance.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mibalance.data.BalanceManager
import com.example.mibalance.data.BalanceWriteResult
import kotlin.math.abs

@Composable
fun BalanceSliderSection(
    balance: Float,
    onBalanceChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    showQuickStepButtons: Boolean = true
) {
    val normalized = BalanceManager.normalizeBalance(balance)
    val primaryColor = MaterialTheme.colorScheme.primary
    val trackBg = MaterialTheme.colorScheme.surfaceVariant

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = BalanceManager.formatBalance(normalized),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Barra visual simétrica desde el centro
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
        ) {
            val radius = size.height / 2f
            drawRoundRect(
                color = trackBg,
                size = size,
                cornerRadius = CornerRadius(radius, radius)
            )
            val centerX = size.width / 2f
            val targetX = centerX + (normalized * (size.width / 2f))
            val leftX = minOf(centerX, targetX)
            val width = abs(targetX - centerX)
            if (width > 1f) {
                drawRoundRect(
                    color = primaryColor,
                    topLeft = Offset(leftX, 0f),
                    size = Size(width, size.height),
                    cornerRadius = CornerRadius(radius, radius)
                )
            }
            // Marca central
            drawCircle(
                color = primaryColor.copy(alpha = 0.6f),
                radius = radius * 0.7f,
                center = Offset(centerX, center.y)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Slider(
            value = normalized,
            onValueChange = { raw ->
                onBalanceChange(BalanceManager.normalizeBalance(raw))
            },
            valueRange = -1f..1f,
            colors = SliderDefaults.colors(
                thumbColor = primaryColor,
                activeTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "L (Izquierda)",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Centro",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "R (Derecha)",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (showQuickStepButtons) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
            ) {
                FilledTonalButton(
                    onClick = { onBalanceChange(BalanceManager.normalizeBalance(normalized - 0.05f)) }
                ) {
                    Text("-5% Izq")
                }
                OutlinedButton(
                    onClick = { onBalanceChange(0f) }
                ) {
                    Text("Centro")
                }
                FilledTonalButton(
                    onClick = { onBalanceChange(BalanceManager.normalizeBalance(normalized + 0.05f)) }
                ) {
                    Text("+5% Der")
                }
            }
        }
    }
}

@Suppress("UNUSED_PARAMETER")
@Composable
fun PermissionNoticeCard(
    permissionIssue: BalanceWriteResult?,
    onRetryPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (permissionIssue !is BalanceWriteResult.Error) return

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "No se pudo aplicar el balance",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = permissionIssue.message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}
