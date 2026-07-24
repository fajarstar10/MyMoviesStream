package com.example.mymovies.feature.home.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mymovies.core.ui.components.GenreChip
import com.example.mymovies.core.ui.components.MovieBanner
import com.example.mymovies.core.ui.components.MovieCard
import com.example.mymovies.core.ui.components.MovieSection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToDetail: (Int) -> Unit,
    onNavigateToGenreList: () -> Unit,
    onNavigateToDiscover: (Int, String) -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val gridState = rememberLazyGridState()

    LaunchedEffect(Unit) {
        if (state.popularMovies.isEmpty()) {
            viewModel.setIntent(HomeContract.Intent.LoadHomeData)
        }
    }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is HomeContract.Effect.NavigationToDetail -> onNavigateToDetail(effect.movieId)
                is HomeContract.Effect.NavigationToGenreList -> onNavigateToGenreList()
                is HomeContract.Effect.NavigationToDiscover -> onNavigateToDiscover(effect.genreId, effect.genreName)
                is HomeContract.Effect.ShowError -> { /* Show Snackbar */ }
            }
        }
    }

    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisibleItemIndex = gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisibleItemIndex >= state.popularMovies.size - 5 && state.canLoadMorePopular && !state.isMoreLoading && state.searchQuery.isEmpty() && state.selectedGenre == null
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) {
            viewModel.setIntent(HomeContract.Intent.LoadMorePopular)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("My Movies") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Search Bar
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = { viewModel.setIntent(HomeContract.Intent.SearchMovie(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Search movies...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                shape = MaterialTheme.shapes.medium
            )

            // Genre Chips
            if (state.genres.isNotEmpty() && state.searchQuery.isEmpty()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(end = 16.dp)
                ) {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        modifier = Modifier.weight(1f).padding(vertical = 8.dp)
                    ) {
                        item {
                            GenreChip(
                                genre = com.example.mymovies.domain.model.Genre(0, "All"),
                                isSelected = state.selectedGenre == null,
                                onClick = { viewModel.setIntent(HomeContract.Intent.OnGenreSelected(null)) },
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        }
                        items(state.genres.take(10)) { genre ->
                            GenreChip(
                                genre = genre,
                                isSelected = state.selectedGenre?.id == genre.id,
                                onClick = { viewModel.setIntent(HomeContract.Intent.OnGenreSelected(genre)) },
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        }
                    }
                    Text(
                        text = "See All",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable { viewModel.setIntent(HomeContract.Intent.OnSeeAllGenresClicked) }
                    )
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                if (state.isLoading && !state.isSearching && state.popularMovies.isEmpty()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                state.errorMessage?.let {
                    Text(text = it, modifier = Modifier.align(Alignment.Center).padding(16.dp), color = MaterialTheme.colorScheme.error)
                }

                if (state.searchQuery.isNotEmpty()) {
                    // Search Results Grid
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(state.searchResults) { movie ->
                            MovieCard(
                                movie = movie,
                                onClick = { viewModel.setIntent(HomeContract.Intent.OnMovieClicked(movie)) },
                                modifier = Modifier.padding(4.dp)
                            )
                        }
                    }
                } else {
                    // Main Content
                    LazyVerticalGrid(
                        state = gridState,
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(bottom = 16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Banner (Featured) - Only show if no genre is selected
                        if (state.selectedGenre == null && state.nowPlayingMovies.isNotEmpty()) {
                            item(span = { GridItemSpan(2) }) {
                                val pagerState = rememberPagerState(pageCount = { 
                                    minOf(state.nowPlayingMovies.size, 5) 
                                })
                                Column {
                                    HorizontalPager(
                                        state = pagerState,
                                        modifier = Modifier.fillMaxWidth()
                                    ) { page ->
                                        MovieBanner(
                                            movie = state.nowPlayingMovies[page],
                                            onClick = { viewModel.setIntent(HomeContract.Intent.OnMovieClicked(state.nowPlayingMovies[page])) }
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        }

                        // Sections - Only show if no genre is selected
                        if (state.selectedGenre == null) {
                            if (state.topRatedMovies.isNotEmpty()) {
                                item(span = { GridItemSpan(2) }) {
                                    MovieSection(
                                        title = "Top Rated",
                                        movies = state.topRatedMovies,
                                        onMovieClick = { viewModel.setIntent(HomeContract.Intent.OnMovieClicked(it)) }
                                    )
                                }
                            }
                        }

                        // Popular / Filtered Results Header
                        item(span = { GridItemSpan(2) }) {
                            Text(
                                text = if (state.selectedGenre == null) "Popular Movies" else "${state.selectedGenre?.name} Movies",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(16.dp)
                            )
                        }

                        // Grid for Popular or Filtered Movies
                        items(state.popularMovies) { movie ->
                            MovieCard(
                                movie = movie,
                                onClick = { viewModel.setIntent(HomeContract.Intent.OnMovieClicked(movie)) },
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        if (state.isMoreLoading) {
                            item(span = { GridItemSpan(2) }) {
                                Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(modifier = Modifier.size(32.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
