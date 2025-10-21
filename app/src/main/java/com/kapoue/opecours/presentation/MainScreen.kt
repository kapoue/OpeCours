package com.kapoue.opecours.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kapoue.opecours.domain.model.Operator
import com.kapoue.opecours.presentation.components.StockTile
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()
    
    // Pull-to-refresh simple avec geste de glissement
    var isRefreshing by remember { mutableStateOf(false) }
    
    LaunchedEffect(state.isRefreshing) {
        isRefreshing = state.isRefreshing
    }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures { _, dragAmount ->
                    if (dragAmount.y > 50 && !isRefreshing) {
                        scope.launch {
                            viewModel.refresh()
                        }
                    }
                }
            }
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp)
        ) {
            // Bouton de refresh manuel en haut
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "OpéCours",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Button(
                    onClick = { viewModel.refresh() },
                    enabled = !state.isRefreshing
                ) {
                    if (state.isRefreshing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("🔄 Actualiser")
                    }
                }
            }
            
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
        
        // Indicateur de refresh en haut
        if (state.isRefreshing) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
            )
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