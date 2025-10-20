package com.kapoue.opecours.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kapoue.opecours.domain.model.Operator
import com.kapoue.opecours.domain.model.Stock
import com.kapoue.opecours.presentation.theme.NegativeRed
import com.kapoue.opecours.presentation.theme.PositiveGreen
import com.kapoue.opecours.util.DateUtils

@Composable
fun StockTile(
    stock: Stock?,
    operator: Operator,
    modifier: Modifier = Modifier
) {
    val isInactive = !operator.isActive
    val surfaceColor = if (isInactive)
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    else
        MaterialTheme.colorScheme.surface
    
    Card(
        modifier = modifier
            .fillMaxSize()
            .padding(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = surfaceColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isInactive) 1.dp else 2.dp)
    ) {
        if (isInactive) {
            // Affichage pour opérateur inactif (SFR uniquement)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = androidx.compose.ui.res.painterResource(operator.logoRes),
                    contentDescription = operator.displayName,
                    tint = operator.colorPrimary.copy(alpha = 0.3f),
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = operator.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Données non disponibles",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        } else if (stock == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = operator.colorPrimary,
                    modifier = Modifier.size(32.dp)
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // En-tête : Logo + Nom + Flèche de tendance
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = androidx.compose.ui.res.painterResource(operator.logoRes),
                            contentDescription = operator.displayName,
                            tint = operator.colorPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = operator.displayName,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = operator.colorPrimary
                        )
                    }
                    Icon(
                        imageVector = if (stock.change >= 0)
                            Icons.Default.TrendingUp
                        else
                            Icons.Default.TrendingDown,
                        contentDescription = "Tendance",
                        tint = if (stock.change >= 0) PositiveGreen else NegativeRed,
                        modifier = Modifier.size(18.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Cours actuel
                Text(
                    text = "%.2f €".format(stock.currentPrice),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Variation % et €
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "(%.2f%%)".format(stock.changePercent),
                        style = MaterialTheme.typography.titleMedium,
                        color = if (stock.change >= 0) PositiveGreen else NegativeRed,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "%+.2f €".format(stock.change),
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (stock.change >= 0) PositiveGreen else NegativeRed
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Mini graphique + Cours veille
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MiniChart(
                        data = stock.historicalPrices,
                        color = operator.colorPrimary,
                        modifier = Modifier
                            .weight(1f)
                            .height(30.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Veille: %.2f€".format(stock.previousClose),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Heure de mise à jour
                Text(
                    text = "MAJ: ${DateUtils.formatTime(stock.lastUpdate)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                // Indicateur marché fermé
                if (!stock.isMarketOpen) {
                    Text(
                        text = "Marché fermé",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}