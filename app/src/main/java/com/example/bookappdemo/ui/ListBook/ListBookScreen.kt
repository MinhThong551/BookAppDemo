package com.example.bookappdemo.ui.ListBook

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
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
fun ListBookScreen(
    books: List<BookData>,
    selectedUiState: BookDetailUiState?,
    selectedBookId: String?,
    onNavigateToAdd: () -> Unit,
    onBookClick: (String) -> Unit,
    onDismissDetail: () -> Unit,
    onDeleteBook: (String) -> Unit,
    onSaveEdit: (BookDetailUiState) -> Unit,
) {
    var searchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current

    val filteredBooks = remember(books, searchQuery) {
        books.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
                    it.author.contains(searchQuery, ignoreCase = true)
        }
    }

    if (selectedUiState != null && selectedBookId != null) {
        var isEditMode by rememberSaveable { mutableStateOf(false) }

        BookDetailDialog(
            bookId = selectedBookId,
            initialUiState = selectedUiState,
            isEditMode = isEditMode,
            onDismiss = {
                isEditMode = false
                onDismissDetail()
            },
            onDelete = { id ->
                onDeleteBook(id)
                Toast.makeText(context, "BOOK DELETED", Toast.LENGTH_SHORT).show()
            },
            onEditClick = { isEditMode = true },
            onSaveClick = { updated ->
                onSaveEdit(updated)
                isEditMode = false
                Toast.makeText(context, "UPDATE SUCCESS", Toast.LENGTH_SHORT).show()
            },
            onCancelEdit = { isEditMode = false }
        )
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(title = { Text("📚 Book Shelf") })
                SearchBar(query = searchQuery, onQueryChange = { searchQuery = it })
            }
        },
        bottomBar = {
            MyBottomNavigationBar(
                selectedTab = BottomNavItem.Home,
                onTabSelected = { if (it == BottomNavItem.Add) onNavigateToAdd() }
            )
        }
    ) { padding ->
        if (filteredBooks.isEmpty()) {
            EmptyState(Modifier.padding(padding))
        } else {
            BookList(
                books = filteredBooks,
                modifier = Modifier.padding(padding),
                onBookClick = onBookClick
            )
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
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(book.title, style = MaterialTheme.typography.titleMedium)
                Text(book.author, color = Color.Gray)
                Text("⭐ ${book.rating}")
            }
        }
    }
}

@Composable
fun EmptyState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize().padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("No books found.\nPlease add data.", style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        placeholder = { Text("🔍 Search book, author...") },
        singleLine = true
    )
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun BookDetailDialog(
    bookId: String,
    initialUiState: BookDetailUiState, // Tên tham số đã chuẩn
    isEditMode: Boolean,
    onDismiss: () -> Unit,
    onDelete: (String) -> Unit,
    onEditClick: () -> Unit,
    onSaveClick: (BookDetailUiState) -> Unit,
    onCancelEdit: () -> Unit
) {
    var uiState by remember(initialUiState) { mutableStateOf(initialUiState) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 700.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (isEditMode) "✏️ Update Book" else "📚 Book Detail",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    item {
                        Text(
                            text = if (isEditMode) "Image URLs (Links)" else "Images",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        if (isEditMode) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                uiState.images.forEachIndexed { index, url ->
                                    ImageLinkRow(
                                        url = url,
                                        onUrlChange = { newUrl ->
                                            val mutableList = uiState.images.toMutableList()
                                            mutableList[index] = newUrl
                                            uiState = uiState.copy(images = mutableList.toList())
                                        },
                                        onDelete = {
                                            val mutableList = uiState.images.toMutableList()
                                            mutableList.removeAt(index)
                                            uiState = uiState.copy(images = mutableList.toList())
                                        }
                                    )
                                }

                                OutlinedButton(
                                    onClick = {
                                        val mutableList = uiState.images.toMutableList()
                                        mutableList.add("")
                                        uiState = uiState.copy(images = mutableList.toList())
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    contentPadding = PaddingValues(vertical = 8.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null)
                                    Spacer(Modifier.width(4.dp))
                                    Text("Add New Image Link")
                                }
                            }

                        } else {
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
                        }
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                    }
                    item {
                        EditableField("Title", uiState.title, isEditMode) {
                            uiState = uiState.copy(title = it)
                        }
                        EditableField("Author", uiState.authorName, isEditMode) {
                            uiState = uiState.copy(authorName = it)
                        }
                    }

                    item {
                        if (isEditMode) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(Modifier.weight(1f)) {
                                    EditableField("Rating", uiState.rating.toString(), true) {
                                        uiState = uiState.copy(rating = it.toDoubleOrNull() ?: 0.0)
                                    }
                                }
                                Box(Modifier.weight(1f)) {
                                    EditableField("Reviews", uiState.ratingCount.toString(), true) {
                                        uiState = uiState.copy(ratingCount = it.toIntOrNull() ?: 0)
                                    }
                                }
                            }
                        } else {
                            Text(
                                text = "⭐ ${uiState.rating} (${uiState.ratingCount} reviews)",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }

                    item {
                        EditableField("Description", uiState.description, isEditMode) {
                            uiState = uiState.copy(description = it)
                        }
                        EditableField("Summary", uiState.summary, isEditMode) {
                            uiState = uiState.copy(summary = it)
                        }
                    }

                    item {
                        EditableField("Language", uiState.language, isEditMode) {
                            uiState = uiState.copy(language = it)
                        }
                        EditableField("Publisher", uiState.publisher, isEditMode) {
                            uiState = uiState.copy(publisher = it)
                        }
                        EditableField("Publish Date", uiState.publishDate, isEditMode) {
                            uiState = uiState.copy(publishDate = it)
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(Modifier.weight(1f)) {
                                EditableField("Pages", uiState.pages.toString(), isEditMode) {
                                    uiState = uiState.copy(pages = it.toIntOrNull() ?: 0)
                                }
                            }
                            Box(Modifier.weight(1f)) {
                                EditableField("Price", uiState.price.toString(), isEditMode) {
                                    uiState = uiState.copy(price = it.toDoubleOrNull() ?: 0.0)
                                }
                            }
                        }
                    }

                    if (!isEditMode) {
                        item {
                            Text(
                                text = "Price: ${uiState.price} ${uiState.currency}",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (isEditMode) {
                        Button(
                            onClick = { onSaveClick(uiState) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                        ) {
                            Text("Lưu")
                        }
                        OutlinedButton(
                            onClick = {
                                uiState = initialUiState // Reset về ban đầu
                                onCancelEdit()
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("ESC")
                        }
                    } else {
                        Button(
                            onClick = onEditClick,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Edit")
                        }
                        Button(
                            onClick = { onDelete(bookId) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Delete")
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun EditableField(
    label: String,
    value: String,
    isEditMode: Boolean,
    onValueChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        if (isEditMode) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                textStyle = MaterialTheme.typography.bodyMedium
            )
        } else {
            Text(
                text = if (value.isBlank()) "N/A" else value,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black.copy(alpha = 0.8f),
                modifier = Modifier.padding(vertical = 4.dp)
            )
            Divider(thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.5f))
        }
    }
}

@Composable
fun Chip(text: String) {
    Box(
        modifier = Modifier
            .padding(end = 8.dp, bottom = 8.dp)
            .clip(RoundedCornerShape(50))
            .background(Color.LightGray.copy(alpha = 0.3f))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(text, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun ImageLinkRow(
    url: String,
    onUrlChange: (String) -> Unit,
    onDelete: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = url,
            onValueChange = onUrlChange,
            modifier = Modifier.weight(1f),
            textStyle = MaterialTheme.typography.bodySmall,
            placeholder = { Text("Paste image URL here...", fontSize = 12.sp) },
            singleLine = true
        )
        IconButton(onClick = onDelete) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Remove Image",
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}