package com.example.mibalance.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.mibalance.model.HeadphoneType

@Composable
fun HeadphoneBadge(
    type: HeadphoneType,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(16.dp))
            .background(type.accentColor.copy(alpha = 0.14f))
            .padding(size * 0.16f),
        contentAlignment = Alignment.Center
    ) {
        HeadphoneIllustration(
            type = type,
            tint = type.accentColor,
            modifier = Modifier.size(size * 0.68f)
        )
    }
}

@Composable
fun HeadphoneIllustration(
    type: HeadphoneType,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = w * 0.085f

        when (type) {
            HeadphoneType.REDMI_BUDS -> {
                // Dos auriculares in-ear compactos con almohadilla
                // Izquierdo
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(w * 0.14f, h * 0.22f),
                    size = Size(w * 0.28f, h * 0.34f),
                    cornerRadius = CornerRadius(w * 0.14f, w * 0.14f)
                )
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(w * 0.23f, h * 0.50f),
                    size = Size(w * 0.11f, h * 0.26f),
                    cornerRadius = CornerRadius(w * 0.05f, w * 0.05f)
                )
                drawCircle(
                    color = tint.copy(alpha = 0.45f),
                    radius = w * 0.08f,
                    center = Offset(w * 0.14f, h * 0.35f)
                )

                // Derecho
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(w * 0.58f, h * 0.22f),
                    size = Size(w * 0.28f, h * 0.34f),
                    cornerRadius = CornerRadius(w * 0.14f, w * 0.14f)
                )
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(w * 0.66f, h * 0.50f),
                    size = Size(w * 0.11f, h * 0.26f),
                    cornerRadius = CornerRadius(w * 0.05f, w * 0.05f)
                )
                drawCircle(
                    color = tint.copy(alpha = 0.45f),
                    radius = w * 0.08f,
                    center = Offset(w * 0.86f, h * 0.35f)
                )
            }

            HeadphoneType.AIRPODS -> {
                // Tallo largo clásico abierto
                // Izquierdo
                drawCircle(
                    color = tint,
                    radius = w * 0.14f,
                    center = Offset(w * 0.28f, h * 0.28f)
                )
                drawLine(
                    color = tint,
                    start = Offset(w * 0.32f, h * 0.38f),
                    end = Offset(w * 0.32f, h * 0.84f),
                    strokeWidth = w * 0.10f,
                    cap = StrokeCap.Round
                )
                // Derecho
                drawCircle(
                    color = tint,
                    radius = w * 0.14f,
                    center = Offset(w * 0.72f, h * 0.28f)
                )
                drawLine(
                    color = tint,
                    start = Offset(w * 0.68f, h * 0.38f),
                    end = Offset(w * 0.68f, h * 0.84f),
                    strokeWidth = w * 0.10f,
                    cap = StrokeCap.Round
                )
            }

            HeadphoneType.AIRPODS_PRO -> {
                // Tallo corto con almohadilla marcada
                // Izquierdo
                drawOval(
                    color = tint.copy(alpha = 0.45f),
                    topLeft = Offset(w * 0.09f, h * 0.23f),
                    size = Size(w * 0.15f, h * 0.20f)
                )
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(w * 0.18f, h * 0.20f),
                    size = Size(w * 0.24f, h * 0.28f),
                    cornerRadius = CornerRadius(w * 0.12f, w * 0.12f)
                )
                drawLine(
                    color = tint,
                    start = Offset(w * 0.33f, h * 0.44f),
                    end = Offset(w * 0.37f, h * 0.74f),
                    strokeWidth = w * 0.10f,
                    cap = StrokeCap.Round
                )

                // Derecho
                drawOval(
                    color = tint.copy(alpha = 0.45f),
                    topLeft = Offset(w * 0.76f, h * 0.23f),
                    size = Size(w * 0.15f, h * 0.20f)
                )
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(w * 0.58f, h * 0.20f),
                    size = Size(w * 0.24f, h * 0.28f),
                    cornerRadius = CornerRadius(w * 0.12f, w * 0.12f)
                )
                drawLine(
                    color = tint,
                    start = Offset(w * 0.67f, h * 0.44f),
                    end = Offset(w * 0.63f, h * 0.74f),
                    strokeWidth = w * 0.10f,
                    cap = StrokeCap.Round
                )
            }

            HeadphoneType.GALAXY_BUDS -> {
                // Forma botón compacto sin tallo
                drawOval(
                    color = tint,
                    topLeft = Offset(w * 0.10f, h * 0.26f),
                    size = Size(w * 0.34f, h * 0.44f)
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.35f),
                    radius = w * 0.08f,
                    center = Offset(w * 0.27f, h * 0.48f)
                )

                drawOval(
                    color = tint,
                    topLeft = Offset(w * 0.56f, h * 0.26f),
                    size = Size(w * 0.34f, h * 0.44f)
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.35f),
                    radius = w * 0.08f,
                    center = Offset(w * 0.73f, h * 0.48f)
                )
            }

            HeadphoneType.OVER_EAR -> {
                // Diadema superior + almohadillas grandes
                drawArc(
                    color = tint,
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(w * 0.14f, h * 0.12f),
                    size = Size(w * 0.72f, h * 0.70f),
                    style = Stroke(width = stroke, cap = StrokeCap.Round)
                )
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(w * 0.08f, h * 0.44f),
                    size = Size(w * 0.20f, h * 0.38f),
                    cornerRadius = CornerRadius(w * 0.10f, w * 0.10f)
                )
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(w * 0.72f, h * 0.44f),
                    size = Size(w * 0.20f, h * 0.38f),
                    cornerRadius = CornerRadius(w * 0.10f, w * 0.10f)
                )
            }

            HeadphoneType.WIRED -> {
                // Dos cápsulas + cable en Y + conector
                drawCircle(
                    color = tint,
                    radius = w * 0.12f,
                    center = Offset(w * 0.28f, h * 0.24f)
                )
                drawCircle(
                    color = tint,
                    radius = w * 0.12f,
                    center = Offset(w * 0.72f, h * 0.24f)
                )
                val path = Path().apply {
                    moveTo(w * 0.28f, h * 0.34f)
                    lineTo(w * 0.50f, h * 0.62f)
                    lineTo(w * 0.72f, h * 0.34f)
                    moveTo(w * 0.50f, h * 0.62f)
                    lineTo(w * 0.50f, h * 0.86f)
                }
                drawPath(
                    path = path,
                    color = tint,
                    style = Stroke(width = w * 0.075f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(w * 0.44f, h * 0.78f),
                    size = Size(w * 0.12f, h * 0.14f),
                    cornerRadius = CornerRadius(w * 0.04f, w * 0.04f)
                )
            }

            HeadphoneType.GENERIC -> {
                drawArc(
                    color = tint,
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(w * 0.18f, h * 0.18f),
                    size = Size(w * 0.64f, h * 0.62f),
                    style = Stroke(width = stroke, cap = StrokeCap.Round)
                )
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(w * 0.13f, h * 0.48f),
                    size = Size(w * 0.16f, h * 0.30f),
                    cornerRadius = CornerRadius(w * 0.08f, w * 0.08f)
                )
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(w * 0.71f, h * 0.48f),
                    size = Size(w * 0.16f, h * 0.30f),
                    cornerRadius = CornerRadius(w * 0.08f, w * 0.08f)
                )
            }
        }
    }
}

@Composable
fun SimplePlusIcon(tint: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val stroke = size.width * 0.13f
        val c = size.width / 2f
        val pad = size.width * 0.2f
        drawLine(tint, Offset(c, pad), Offset(c, size.height - pad), stroke, StrokeCap.Round)
        drawLine(tint, Offset(pad, c), Offset(size.width - pad, c), stroke, StrokeCap.Round)
    }
}

@Composable
fun SimpleHeadphonesIcon(tint: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = w * 0.11f
        drawArc(
            color = tint,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(w * 0.16f, h * 0.16f),
            size = Size(w * 0.68f, h * 0.64f),
            style = Stroke(width = stroke, cap = StrokeCap.Round)
        )
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.10f, h * 0.48f),
            size = Size(w * 0.18f, h * 0.32f),
            cornerRadius = CornerRadius(w * 0.08f, w * 0.08f)
        )
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.72f, h * 0.48f),
            size = Size(w * 0.18f, h * 0.32f),
            cornerRadius = CornerRadius(w * 0.08f, w * 0.08f)
        )
    }
}

@Composable
fun SimpleSlidersIcon(tint: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = w * 0.10f
        // Tres líneas con controles
        drawLine(tint, Offset(w * 0.12f, h * 0.25f), Offset(w * 0.88f, h * 0.25f), stroke, StrokeCap.Round)
        drawCircle(tint, radius = w * 0.10f, center = Offset(w * 0.34f, h * 0.25f))

        drawLine(tint, Offset(w * 0.12f, h * 0.50f), Offset(w * 0.88f, h * 0.50f), stroke, StrokeCap.Round)
        drawCircle(tint, radius = w * 0.10f, center = Offset(w * 0.68f, h * 0.50f))

        drawLine(tint, Offset(w * 0.12f, h * 0.75f), Offset(w * 0.88f, h * 0.75f), stroke, StrokeCap.Round)
        drawCircle(tint, radius = w * 0.10f, center = Offset(w * 0.46f, h * 0.75f))
    }
}

@Composable
fun SimpleEditIcon(tint: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = w * 0.11f
        drawLine(
            color = tint,
            start = Offset(w * 0.22f, h * 0.78f),
            end = Offset(w * 0.76f, h * 0.24f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
        drawLine(
            color = tint,
            start = Offset(w * 0.18f, h * 0.82f),
            end = Offset(w * 0.36f, h * 0.78f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun SimpleDeleteIcon(tint: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = w * 0.10f
        // Tapa
        drawLine(tint, Offset(w * 0.20f, h * 0.28f), Offset(w * 0.80f, h * 0.28f), stroke, StrokeCap.Round)
        drawLine(tint, Offset(w * 0.38f, h * 0.18f), Offset(w * 0.62f, h * 0.18f), stroke, StrokeCap.Round)
        // Cuerpo
        val path = Path().apply {
            moveTo(w * 0.28f, h * 0.34f)
            lineTo(w * 0.33f, h * 0.82f)
            lineTo(w * 0.67f, h * 0.82f)
            lineTo(w * 0.72f, h * 0.34f)
        }
        drawPath(path, tint, style = Stroke(width = stroke, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}
