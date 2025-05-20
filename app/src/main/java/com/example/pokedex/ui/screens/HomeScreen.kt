package com.example.pokedex.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.pokedex.model.AuthState
import com.example.pokedex.model.Pokemon
import com.example.pokedex.model.PokemonTypes
import com.example.pokedex.navigation.Screen
import com.example.pokedex.ui.components.TypeFilterDialog
import com.example.pokedex.viewmodel.AuthViewModel
import com.example.pokedex.viewmodel.HomeViewModel
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import com.example.pokedex.ui.theme.getTypeColor
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.LaunchedEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    val filteredPokemonList by homeViewModel.filteredPokemonList.collectAsState()
    val isLoading by homeViewModel.isLoading.collectAsState()
    val error by homeViewModel.error.collectAsState()
    val authState by authViewModel.authState.collectAsState()
    val likedPokemonIds by homeViewModel.likedPokemonIds.collectAsState()
    val searchQuery by homeViewModel.searchQuery.collectAsState()
    val selectedType by homeViewModel.selectedType.collectAsState()
    
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var isSearchActive by remember { mutableStateOf(false) }
    var showFilterDialog by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    if (isSearchActive) {
                        TextField(
                            value = searchQuery,
                            onValueChange = { homeViewModel.setSearchQuery(it) },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Search Pokémon...") },
                            singleLine = true,
                            colors = TextFieldDefaults.textFieldColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                                unfocusedIndicatorColor = MaterialTheme.colorScheme.surface
                            )
                        )
                    } else {
                        Text("Pokédex")
                    }
                },
                actions = {
                    if (isSearchActive) {
                        IconButton(
                            onClick = {
                                isSearchActive = false
                                homeViewModel.setSearchQuery("")
                            }
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close Search")
                        }
                    } else {
                        IconButton(onClick = { isSearchActive = true }) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                    }
                    
                    IconButton(onClick = { showFilterDialog = true }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter")
                    }
                    
                    IconButton(
                        onClick = {
                            when (authState) {
                                is AuthState.Success -> {
                                    navController.navigate(Screen.Profile.route)
                                }
                                else -> {
                                    navController.navigate(Screen.Login.route)
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile"
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
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                error != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = error ?: "Unknown error",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Button(
                            onClick = { homeViewModel.loadPokemonList() },
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text("Retry")
                        }
                    }
                }
                filteredPokemonList.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "No Pokémon found matching your criteria",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        if (selectedType != PokemonTypes.All) {
                            TextButton(
                                onClick = { homeViewModel.setSelectedType(PokemonTypes.All) }
                            ) {
                                Text("Clear Filter")
                            }
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredPokemonList) { pokemon ->
                            PokemonListItem(
                                pokemon = pokemon,
                                isLiked = likedPokemonIds.contains(pokemon.id.toString()),
                                onLikeClick = {
                                    when (authState) {
                                        is AuthState.Success -> {
                                            homeViewModel.toggleLikePokemon(
                                                pokemon.id.toString(),
                                                pokemon.name
                                            )
                                        }
                                        else -> {
                                            scope.launch {
                                                snackbarHostState.showSnackbar(
                                                    message = "Please log in to like Pokémon",
                                                    duration = SnackbarDuration.Short
                                                )
                                            }
                                        }
                                    }
                                },
                                onItemClick = {
                                    navController.navigate(Screen.Detail.createRoute(pokemon.name))
                                }
                            )
                        }
                        item {
                            LaunchedEffect(filteredPokemonList.size) {
                                if (listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == filteredPokemonList.lastIndex && filteredPokemonList.isNotEmpty()) {
                                    homeViewModel.loadMore()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showFilterDialog) {
        TypeFilterDialog(
            selectedType = selectedType,
            onTypeSelected = { type ->
                homeViewModel.setSelectedType(type)
            },
            onDismiss = { showFilterDialog = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonListItem(
    pokemon: Pokemon,
    isLiked: Boolean,
    onLikeClick: () -> Unit,
    onItemClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accentColor = getTypeColor(pokemon.types.firstOrNull()?.type?.name ?: "")
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onItemClick),
        colors = CardDefaults.cardColors(containerColor = accentColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = pokemon.imageUrl,
                    contentDescription = pokemon.name,
                    modifier = Modifier.size(60.dp),
                    contentScale = ContentScale.Fit
                )
                Text(
                    text = pokemon.name.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
            }
            IconButton(onClick = onLikeClick) {
                Icon(
                    imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = if (isLiked) "Unlike" else "Like",
                    tint = if (isLiked) MaterialTheme.colorScheme.primary else Color.White
                )
            }
        }
    }
} 