package com.example.bookappdemo.ui.listBook

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.bookappdemo.data.model.BookData
import com.example.bookappdemo.ui.base.BookDetailUiState
import com.example.bookappdemo.ui.components.BottomNavItem
import com.example.bookappdemo.ui.components.MyBottomNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListBookFirestoreScreen(
    books: List<BookData>,
    selectedUiState: BookDetailUiState?,
    isLoading: Boolean,
    onNavigateHome: () -> Unit,
    onNavigateToAdd: () -> Unit,
    onBookClick: (String) -> Unit,
    onDismissDetail: () -> Unit,
    onSearchLocal: (String) -> Unit
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }

    val filteredBooks = remember(books, searchQuery) {
        if (searchQuery.isBlank()) books
        else books.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
                    it.author.contains(searchQuery, ignoreCase = true)
        }
    }

    if (selectedUiState != null) {
        FirestoreBookDetailDialog(
            uiState = selectedUiState,
            onDismiss = onDismissDetail
        )
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(title = { Text("🔥 Firestore Books") })

                FirestoreSearchBar(
                    query = searchQuery,
                    onQueryChange = {
                        searchQuery = it
                        onSearchLocal(it)
                    },
                    onSearchClick = { /* Search Local tự động */ },
                    isLoading = isLoading
                )

                if (isLoading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
            }
        },
        bottomBar = {
            MyBottomNavigationBar(
                selectedTab = BottomNavItem.FireStore,
                onTabSelected = { item ->
                    if (item == BottomNavItem.Home) onNavigateHome()
                    else if (item == BottomNavItem.Add) onNavigateToAdd()

                }
            )
        }
    ) { padding ->
        if (filteredBooks.isEmpty() && !isLoading) {
            FirestoreEmptyState(Modifier.padding(padding))
        } else {
            FirestoreBookList(
                books = filteredBooks,
                modifier = Modifier.padding(padding),
                onBookClick = onBookClick
            )
        }
    }
}


@Composable
fun FirestoreBookList(
    books: List<BookData>,
    onBookClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(items = books, key = { book -> book.id }) { book ->
            FirestoreBookCard(
                book = book,
                onClick = { onBookClick(book.id) }
            )
        }
    }
}

@Composable
fun FirestoreBookCard(
    book: BookData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            AsyncImage(
                model = book.image,
                contentDescription = book.title,
                modifier = Modifier
                    .size(100.dp, 120.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(15.dp))
            Column(Modifier.weight(1f)) {
                Text(book.title, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(15.dp))
                Text(book.author, color = Color.Gray)
                Spacer(Modifier.height(15.dp))
                Text("⭐ ${book.rating}")
            }
        }
    }
}

@Composable
fun FirestoreEmptyState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize().padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("No books found in Firestore.", style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun FirestoreSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearchClick: () -> Unit,
    isLoading: Boolean
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        placeholder = { Text("Text Key (Ex : Book, Author)...") },
        singleLine = true,
        trailingIcon = {
            IconButton(onClick = onSearchClick, enabled = !isLoading) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    Icon(Icons.Default.Search, contentDescription = "Search" )
                }
            }
        },
        label = { Text("Find Book or Author") }
    )
}


@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FirestoreBookDetailDialog(
    uiState: BookDetailUiState,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 700.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .offset(x = (-8).dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Dialog",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = "🔥 Firestore Book",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        if (uiState.images.isNotEmpty()) {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(uiState.images) { imageUrl ->
                                    AsyncImage(
                                        model = imageUrl,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .width(120.dp)
                                            .height(180.dp)
                                            .clip(RoundedCornerShape(8.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp)
                                    .background(
                                        Color.LightGray.copy(0.3f),
                                        RoundedCornerShape(8.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No Images available", color = Color.Gray)
                            }
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    }

                    item {
                        FirestoreReadOnlyField("Title", uiState.title)
                        FirestoreReadOnlyField("Author", uiState.authorName)
                    }

                    item {
                        Text(
                            text = "⭐ ${uiState.rating} (${uiState.ratingCount} reviews)",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    item {
                        FirestoreReadOnlyField("Description", uiState.description)
                        FirestoreReadOnlyField("Summary", uiState.summary)
                    }

                    item {
                        FirestoreReadOnlyField("Language", uiState.language)
                        FirestoreReadOnlyField("Publisher", uiState.publisher)
                        FirestoreReadOnlyField("Publish Date", uiState.publishDate)

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(Modifier.weight(1f)) {
                                FirestoreReadOnlyField("Pages", uiState.pages.toString())
                            }
                            Box(Modifier.weight(1f)) {
                                FirestoreReadOnlyField("Price", uiState.price.toString())
                            }
                        }
                    }

                    item {
                        Text(
                            text = "Price: ${uiState.price} ${uiState.currency}",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }

            }
        }
    }
}

@Composable
fun FirestoreReadOnlyField(
    label: String,
    value: String
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = if (value.isBlank()) "N/A" else value,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Black.copy(alpha = 0.8f),
            modifier = Modifier.padding(vertical = 4.dp)
        )
        HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.5f))
    }
}