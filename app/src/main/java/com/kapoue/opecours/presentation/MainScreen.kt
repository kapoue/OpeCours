package com.kapoue.opecours.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.background
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kapoue.opecours.domain.model.Operator
import com.kapoue.opecours.presentation.components.StockTile
import com.kapoue.opecours.util.DebugUtils
import kotlinx.coroutines.delay

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var isRefreshing by remember { mutableStateOf(false) }
    var pullOffset by remember { mutableStateOf(0f) }
    val density = LocalDensity.current
    
    // Gérer le pull-to-refresh manuellement
    LaunchedEffect(state.isRefreshing) {
        isRefreshing = state.isRefreshing
        if (!state.isRefreshing) {
            pullOffset = 0f
        }
    }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        DebugUtils.logInfo("🔄 Début du drag pour pull-to-refresh")
                    },
                    onDragEnd = {
                        DebugUtils.logInfo("🔄 Fin du drag, offset: $pullOffset")
                        if (pullOffset > with(density) { 100.dp.toPx() }) {
                            DebugUtils.logInfo("🔄 Pull-to-refresh déclenché par l'utilisateur")
                            viewModel.refresh()
                        }
                        pullOffset = 0f
                    }
                ) { _, dragAmount ->
                    if (dragAmount.y > 0) {
                        pullOffset = (pullOffset + dragAmount.y).coerceAtMost(with(density) { 150.dp.toPx() })
                        DebugUtils.logInfo("🔄 Drag en cours, offset: $pullOffset")
                    }
                }
            }
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp)
        ) {
            // Première ligne : Orange et Bouygues
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StockTile(
                    stock = state.stocks.find { it.symbol == "ORA.PA" },
                    operator = Operator.ORANGE,
                    modifier = Modifier.weight(1f)
                )
                StockTile(
                    stock = state.stocks.find { it.symbol == "EN.PA" },
                    operator = Operator.BOUYGUES,
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Deuxième ligne : SFR et Free
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StockTile(
                    stock = state.stocks.find { it.symbol == "ATC.PA" },
                    operator = Operator.SFR,
                    modifier = Modifier.weight(1f)
                )
                StockTile(
                    stock = state.stocks.find { it.symbol == "ILD.PA" },
                    operator = Operator.FREE,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        // Indicateur de pull-to-refresh personnalisé
        if (pullOffset > 0f || state.isRefreshing) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = with(density) { (pullOffset / 3).toDp() })
                    .size(40.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = androidx.compose.foundation.shape.CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (state.isRefreshing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "↓",
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
        
        // Affichage des erreurs
        state.error?.let { error ->
            LaunchedEffect(error) {
                // Afficher l'erreur pendant 3 secondes puis la masquer
                kotlinx.coroutines.delay(3000)
                viewModel.clearError()
            }
            
            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                action = {
                    TextButton(onClick = { viewModel.clearError() }) {
                        Text("OK")
                    }
                }
            ) {
                Text(error)
            }
        }
        
        // Indicateur de chargement initial
        if (state.isLoading && state.stocks.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}