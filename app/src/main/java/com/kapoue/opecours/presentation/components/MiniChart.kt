package com.kapoue.opecours.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun MiniChart(
    data: List<Double>,
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        if (data.isEmpty() || data.size < 2) return@Canvas
        
        val min = data.minOrNull() ?: 0.0
        val max = data.maxOrNull() ?: 1.0
        val range = max - min
        if (range <= 0.0) return@Canvas
        
        val stepX = size.width / (data.size - 1)
        val path = Path()
        
        data.forEachIndexed { index, value ->
            val x = index * stepX
            val y = size.height - ((value - min) / range * size.height).toFloat()
            
            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }
        
        drawPath(
            path = path,
            color = color,
            style = Stroke(
                width = 2.dp.toPx(),
                cap = StrokeCap.Round
            )
        )
        
        // Ajouter des points aux extrémités pour plus de clarté
        data.forEachIndexed { index, value ->
            val x = index * stepX
            val y = size.height - ((value - min) / range * size.height).toFloat()
            
            if (index == 0 || index == data.size - 1) {
                drawCircle(
                    color = color,
                    radius = 3.dp.toPx(),
                    center = androidx.compose.ui.geometry.Offset(x, y)
                )
            }
        }
    }
}