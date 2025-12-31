package com.example.bookappdemo.ui.listBook

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.bookappdemo.data.model.BookData
import com.example.bookappdemo.ui.base.BookDetailUiState
import com.example.bookappdemo.ui.components.BottomNavItem
import com.example.bookappdemo.ui.components.MyBottomNavigationBar
import com.example.bookappdemo.utils.clearFocusOnTap

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
    val focusManager = LocalFocusManager.current

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
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(bottom = 8.dp)
            ) {
                CenterAlignedTopAppBar(
                    title = { Text("🔥 Firestore Books", fontWeight = FontWeight.Bold, color = Color(0xFFD84315)) }, // Màu cam lửa
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )

                StylishSearchBar(
                    query = searchQuery,
                    onQueryChange = {
                        searchQuery = it
                        onSearchLocal(it)
                    },
                    onSearchClick = {
                        focusManager.clearFocus()
                    },
                    isLoading = isLoading
                )

                if (isLoading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth().height(2.dp))
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
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .clearFocusOnTap(focusManager)
        ) {
            if (filteredBooks.isEmpty() && !isLoading) {
                FirestoreEmptyState()
            } else {
                FirestoreBookList(
                    books = filteredBooks,
                    onBookClick = {
                        focusManager.clearFocus()
                        onBookClick(it)
                    }
                )
            }
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
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
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
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                shadowElevation = 4.dp,
                modifier = Modifier.size(90.dp, 130.dp)
            ) {
                AsyncImage(
                    model = book.image,
                    contentDescription = book.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    error = rememberVectorPainter(Icons.Default.AddCircle)
                )
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f).height(130.dp)) {
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = book.author,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Spacer(Modifier.weight(1f))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, null, tint = Color(0xFFFFC107), modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("${book.rating}", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun FirestoreEmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(80.dp), tint = Color.Gray)
        Spacer(Modifier.height(16.dp))
        Text("No books found in Firestore.", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FirestoreBookDetailDialog(
    uiState: BookDetailUiState,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .heightIn(max = 750.dp),
            elevation = CardDefaults.cardElevation(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "🔥 Firestore Details",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFFD84315)
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(50))
                            .size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                HorizontalDivider(color = Color.LightGray.copy(0.3f))
                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Text(
                            text = "Images",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        if (uiState.images.isNotEmpty()) {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                items(uiState.images) { imageUrl ->
                                    AsyncImage(
                                        model = imageUrl,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .width(110.dp)
                                            .height(160.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .shadow(4.dp, RoundedCornerShape(12.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp)
                                    .background(Color.LightGray.copy(0.2f), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No images available",
                                    color = Color.Gray,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                )
                            }
                        }
                    }

                    item {
                        Text(
                            text = if (uiState.title.isBlank()) "No Title" else uiState.title,
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = if (uiState.authorName.isBlank()) "Unknown Author" else uiState.authorName,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        HorizontalDivider(modifier = Modifier.padding(top = 12.dp), color = Color.LightGray.copy(0.2f))
                    }

                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFFFC107),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "${uiState.rating}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "(${uiState.ratingCount} reviews)", // Nếu model chưa có thì bỏ dòng này
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                        }
                    }

                    item {
                        ReadOnlyField(label = "Description", value = uiState.description)
                        ReadOnlyField(label = "Summary", value = uiState.summary)
                    }
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Column(Modifier.weight(1f)) {
                                ReadOnlyField("Language", uiState.language)
                                ReadOnlyField("Publisher", uiState.publisher)
                            }
                            Column(Modifier.weight(1f)) {
                                ReadOnlyField("Pages", "${uiState.pages}")
                                ReadOnlyField("Published Date", uiState.publishDate)
                            }
                        }
                    }
                    item {
                        Surface(
                            color = Color(0xFFD84315).copy(alpha = 0.1f), // Màu nền cam nhạt
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                        ) {
                            Text(
                                text = "Price: ${uiState.price} ${uiState.currency}",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFD84315)
                                ),
                                modifier = Modifier.padding(16.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReadOnlyField(
    label: String,
    value: String
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
            fontWeight = FontWeight.Bold
        )
        Text(
            text = if (value.isBlank()) "N/A" else value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
            modifier = Modifier.padding(top = 2.dp)
        )
        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f), modifier = Modifier.padding(top = 4.dp))
    }
}