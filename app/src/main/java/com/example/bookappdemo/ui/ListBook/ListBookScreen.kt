package com.example.bookappdemo.ui.ListBook

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bookappdemo.data.model.BookData
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.bookappdemo.data.model.BookDetail

//@Composable
//fun ListBookScreen(viewModel: ListBookViewModel) {
//    val books by viewModel.books.collectAsState()
//
//    LazyColumn {
//        items(books) { book ->
//            Column {
//                Text(text = book.title)
//                Text(text = book.author)
//                Text(text = "Rating: ${book.rating}")
//                Text(text = book.image)
//            }
//        }
//    }
//}
enum class BottomNavItem(val title: String) {
    Home("Home"),
    Setting("Setting")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListBookScreen(
    viewModel: ListBookViewModel
) {
    var selectedTab by remember { mutableStateOf(BottomNavItem.Home) }
    var searchQuery by remember { mutableStateOf("") }

    val books by viewModel.books.collectAsState()

    val filteredBooks = books.filter {
        it.title.contains(searchQuery, ignoreCase = true) ||
                it.author.contains(searchQuery, ignoreCase = true)
    }
    val selectedDetail = viewModel.selectedBookDetail

    if (selectedDetail != null) {
        BookDetailDialog(
            detail = selectedDetail,
            onDismiss = { viewModel.dismissDetail() }
        )
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("📚 Book Shelf") }
                )

                SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it }
                )
            }
        },
        bottomBar = {
            NavigationBar {
                BottomNavItem.entries.forEach { item ->
                    NavigationBarItem(
                        selected = selectedTab == item,
                        onClick = { selectedTab = item },
                        label = { Text(item.title) },
                        icon = {
                            Icon(
                                imageVector = when (item) {
                                    BottomNavItem.Home -> Icons.Default.Home
                                    BottomNavItem.Setting -> Icons.Default.Settings
                                },
                                contentDescription = null
                            )
                        }
                    )
                }
            }
        }
    ) { padding ->
        when (selectedTab) {
            BottomNavItem.Home -> {
                if (filteredBooks.isEmpty()) {
                    EmptyState(Modifier.padding(padding))
                } else {
                    BookList(
                        books = filteredBooks,
                        modifier = Modifier.padding(padding),
                        onBookClick = { bookId ->
                            viewModel.onBookClick(bookId)
                        }
                    )
                }
            }


            BottomNavItem.Setting -> {
                SettingScreen(Modifier.padding(padding))
            }
        }
    }
}

@Composable
fun BookList(
    books: List<BookData>,
    onBookClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(books) { book ->
            BookCard(
                book = book,
                onClick = { onBookClick(book.id) }
            )
        }
    }
}
@Composable
fun BookCard(
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
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    book.title,
                    style = MaterialTheme.typography.titleMedium)
                Text(
                    book.author,
                    color = Color.Gray)
                Text("⭐ ${book.rating}")
            }
        }
    }
}


@Composable
fun EmptyState(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No books found.\nPlease add data.",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        placeholder = {
            Text("🔍 Search book,author...")
        },
        singleLine = true
    )
}

@Composable
fun SettingScreen(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("⚙ Settings")
    }
}
@Composable
fun BookDetailDialog(
    detail: BookDetail,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
                .height(800.dp)
        ) {
            Column(Modifier.padding(16.dp)) {

                if (detail.images.isNotEmpty()) {
                    AsyncImage(
                        model = detail.images.first().url,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                }

                Spacer(Modifier.height(12.dp))

//                Text(
//                    text = "⭐ ${detail.rating}",
//                    style = MaterialTheme.typography.titleMedium
//                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = detail.description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

