package com.example.pokedex.ui.screens

// Material Design imports
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Surface
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.CardDefaults

// Foundation imports
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.Image
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource

// Icons imports
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder

// Runtime imports
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope

// UI imports
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

// Navigation
import androidx.navigation.NavController

// ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

// Project imports
import com.example.pokedex.model.AuthState
import com.example.pokedex.model.PokemonDetailsUiState
import com.example.pokedex.model.TypeInfo
import com.example.pokedex.ui.theme.getTypeColor
import com.example.pokedex.viewmodel.AuthViewModel
import com.example.pokedex.viewmodel.DetailViewModel
import com.example.pokedex.viewmodel.HomeViewModel

// Coil
import coil.compose.AsyncImage

// Coroutines
import kotlinx.coroutines.launch

// Add this import
import androidx.compose.material3.SnackbarDuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import com.example.pokedex.R

// NOTE: Make sure you have a pokeball_watermark.png in your res/drawable folder for the watermark to appear.

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    pokemonName: String,
    navController: NavController,
    detailViewModel: DetailViewModel = viewModel()
) {
    val uiState by detailViewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(pokemonName) {
        detailViewModel.loadPokemonDetails(pokemonName)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(pokemonName.replaceFirstChar { it.uppercase() }) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (uiState) {
                PokemonDetailsUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is PokemonDetailsUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = (uiState as PokemonDetailsUiState.Error).message,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Button(
                            onClick = { 
                                scope.launch {
                                    detailViewModel.loadPokemonDetails(pokemonName)
                                }
                            },
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text("Retry")
                        }
                    }
                }
                is PokemonDetailsUiState.Success -> {
                    val pokemon = (uiState as PokemonDetailsUiState.Success).pokemon
                    val accentColor = getTypeColor(pokemon.types.firstOrNull()?.type?.name ?: "")
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Name and sprites
                        Text(
                            text = pokemon.name.replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            AnimatedVisibility(
                                visible = true,
                                enter = fadeIn(animationSpec = tween(900))
                            ) {
                                AsyncImage(
                                    model = pokemon.sprites.frontDefault,
                                    contentDescription = "Front view of ${pokemon.name}",
                                    modifier = Modifier.size(150.dp),
                                    contentScale = ContentScale.Fit
                                )
                            }
                            pokemon.sprites.backDefault?.let { backSprite ->
                                AnimatedVisibility(
                                    visible = true,
                                    enter = fadeIn(animationSpec = tween(900))
                                ) {
                                    AsyncImage(
                                        model = backSprite,
                                        contentDescription = "Back view of ${pokemon.name}",
                                        modifier = Modifier.size(150.dp),
                                        contentScale = ContentScale.Fit
                                    )
                                }
                            }
                        }
                        // Type chips
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            pokemon.types.forEach { typeSlot ->
                                AssistChip(
                                    onClick = {},
                                    label = {
                                        Text(
                                            text = typeSlot.type.name.replaceFirstChar { it.uppercase() },
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    },
                                    colors = AssistChipDefaults.assistChipColors(
                                        containerColor = getTypeColor(typeSlot.type.name),
                                        labelColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(50),
                                    elevation = AssistChipDefaults.elevatedAssistChipElevation(6.dp),
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        // Basic Info
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "Basic Info",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = accentColor
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                DetailRow("Height", "${pokemon.height / 10.0} m")
                                DetailRow("Weight", "${pokemon.weight / 10.0} kg")
                                DetailRow("Base Experience", pokemon.baseExperience.toString())
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Stats
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "Base Stats",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = accentColor
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                pokemon.stats.forEach { stat ->
                                    StatBar(
                                        statName = stat.stat.name.replaceFirstChar { it.uppercase() },
                                        value = stat.baseStat,
                                        maxValue = 255,
                                        barColor = accentColor
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Abilities
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "Abilities",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = accentColor
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                pokemon.abilities.forEach { ability ->
                                    Text(
                                        text = "${ability.ability.name.replaceFirstChar { it.uppercase() }}${if (ability.isHidden) " (Hidden)" else ""}",
                                        style = MaterialTheme.typography.bodyLarge,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Moves
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "Moves",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = accentColor
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                pokemon.moves.take(10).forEach { move ->
                                    Text(
                                        text = move.move.name.replaceFirstChar { it.uppercase() },
                                        style = MaterialTheme.typography.bodyLarge,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )
                                }
                                if (pokemon.moves.size > 10) {
                                    Text(
                                        text = "... and ${pokemon.moves.size - 10} more",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                        modifier = Modifier.padding(top = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
                else -> {
                    Text("Unknown state", modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun TypeChip(
    type: TypeInfo,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = getTypeColor(type.name)
    ) {
        Text(
            text = type.name.replaceFirstChar { it.uppercase() },
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun StatBar(
    statName: String,
    value: Int,
    maxValue: Int,
    barColor: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = value.toFloat() / maxValue,
        animationSpec = tween(durationMillis = 900)
    )
    // Gradient color based on value
    val gradientColor = when {
        value >= 180 -> Color(0xFF43EA5F) // green
        value >= 100 -> Color(0xFFF9CF30) // yellow
        else -> Color(0xFFF57D31) // red
    }
    Column(modifier = modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = statName,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.bodyMedium
            )
        }
        LinearProgressIndicator(
            progress = animatedProgress,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = gradientColor,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
} 