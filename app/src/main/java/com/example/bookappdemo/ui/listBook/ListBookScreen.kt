package com.example.bookappdemo.ui.listBook

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
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
fun ListBookScreen(
    books: List<BookData>,
    selectedUiState: BookDetailUiState?,
    onNavigateToAdd: () -> Unit,
    onBookClick: (String) -> Unit,
    onDismissDetail: () -> Unit,
    onDeleteBook: (String) -> Unit,
    onSaveEdit: (BookDetailUiState) -> Unit,
    isLoading: Boolean,
    onSearchOnline: (String) -> Unit,
    onNavigateToFirestore: () -> Unit,
    onRefresh: () -> Unit
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val pullRefreshState = rememberPullToRefreshState()
    val filteredBooks = remember(books, searchQuery) {
        books.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
                    it.author.contains(searchQuery, ignoreCase = true)
        }
    }

    if (selectedUiState != null) {
        var isEditMode by rememberSaveable { mutableStateOf(false) }

        BookDetailDialog(
            bookId = selectedUiState.id,
            initialUiState = selectedUiState,
            isEditMode = isEditMode,
            onDismiss = {
                isEditMode = false
                onDismissDetail()
            },
            onDelete = { id -> onDeleteBook(id) },
            onEditClick = { isEditMode = true },
            onSaveClick = { updated ->
                onSaveEdit(updated)
                isEditMode = false
            },
            onCancelEdit = { isEditMode = false }
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
                    title = {
                        Text(
                            "📚 Book Shelf",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
                StylishSearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onSearchClick = {
                        focusManager.clearFocus()
                        onSearchOnline(searchQuery)
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
                selectedTab = BottomNavItem.Home,
                onTabSelected = {
                    if (it == BottomNavItem.Add) onNavigateToAdd()
                    else if (it == BottomNavItem.FireStore) onNavigateToFirestore()
                }
            )
        }
    ) { padding ->


        PullToRefreshBox(
            isRefreshing = isLoading,
            onRefresh = { onRefresh() },
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .clearFocusOnTap(focusManager)
        ) {
            if (filteredBooks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    EmptyState()
                }
            } else {
                BookList(
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
fun BookList(
    books: List<BookData>,
    onBookClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 12.dp, horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = books,
            key = { book -> book.id }
        ) { book ->
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
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp, pressedElevation = 2.dp),
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

            Column(
                modifier = Modifier
                    .weight(1f)
                    .height(130.dp)
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = book.author,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.weight(1f))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "${book.rating}",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Menu,
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No books found",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.secondary
        )
        Text(
            text = "Try searching online or add a new one.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
    }
}

@Composable
fun StylishSearchBar(
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
            .padding(horizontal = 16.dp, vertical = 4.dp),
        placeholder = { Text("Search Title, Author...", fontSize = 14.sp) },
        singleLine = true,
        shape = RoundedCornerShape(24.dp),
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
        trailingIcon = {
            IconButton(onClick = onSearchClick, enabled = !isLoading) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Go", tint = MaterialTheme.colorScheme.primary)
                }
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    )
}


@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun BookDetailDialog(
    bookId: String,
    initialUiState: BookDetailUiState,
    isEditMode: Boolean,
    onDismiss: () -> Unit,
    onDelete: (String) -> Unit,
    onEditClick: () -> Unit,
    onSaveClick: (BookDetailUiState) -> Unit,
    onCancelEdit: () -> Unit
) {
    var uiState by rememberSaveable(initialUiState) { mutableStateOf(initialUiState) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth(0.90f)
                .heightIn(max = 750.dp),
            elevation = CardDefaults.cardElevation(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (isEditMode) "✏️ Update Book" else "📖 Book Details",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(50))
                            .size(36.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray.copy(alpha = 0.5f))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Images Section
                    item {
                        Text("Images", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color.Gray)
                        Spacer(modifier = Modifier.height(8.dp))

                        if (isEditMode) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                uiState.images.forEachIndexed { index, url ->
                                    ImageLinkRow(
                                        url = url,
                                        onUrlChange = { newUrl ->
                                            val mutableList = uiState.images.toMutableList()
                                            mutableList[index] = newUrl
                                            uiState = uiState.copy(images = mutableList)
                                        },
                                        onDelete = {
                                            val mutableList = uiState.images.toMutableList()
                                            mutableList.removeAt(index)
                                            uiState = uiState.copy(images = mutableList)
                                        }
                                    )
                                }
                                OutlinedButton(
                                    onClick = {
                                        val mutableList = uiState.images.toMutableList()
                                        mutableList.add("")
                                        uiState = uiState.copy(images = mutableList)
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    border = null
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    Text("Add Image URL", color = MaterialTheme.colorScheme.primary)
                                }
                            }
                        } else {
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
                                    Text("No images available", color = Color.Gray, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                                }
                            }
                        }
                    }

                    item {
                        EditableField("Title", uiState.title, isEditMode) { uiState = uiState.copy(title = it) }
                        EditableField("Author", uiState.authorName, isEditMode) { uiState = uiState.copy(authorName = it) }
                    }

                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(Modifier.weight(1f)) {
                                EditableField("Rating ⭐", uiState.rating.toString(), isEditMode) {
                                    uiState = uiState.copy(rating = it.toDoubleOrNull() ?: 0.0)
                                }
                            }
                            Box(Modifier.weight(1f)) {
                                EditableField("Reviews", uiState.ratingCount.toString(), isEditMode) {
                                    uiState = uiState.copy(ratingCount = it.toIntOrNull() ?: 0)
                                }
                            }
                        }
                    }

                    item {
                        EditableField("Description", uiState.description, isEditMode, singleLine = false) { uiState = uiState.copy(description = it) }
                    }

                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(Modifier.weight(1f)) {
                                EditableField("Language", uiState.language, isEditMode) { uiState = uiState.copy(language = it) }
                            }
                            Box(Modifier.weight(1f)) {
                                EditableField("Pages", uiState.pages.toString(), isEditMode) { uiState = uiState.copy(pages = it.toIntOrNull() ?: 0) }
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(Modifier.weight(1f)) {
                                EditableField("Publisher", uiState.publisher, isEditMode) { uiState = uiState.copy(publisher = it) }
                            }
                            Box(Modifier.weight(1f)) {
                                EditableField("Published", uiState.publishDate, isEditMode) { uiState = uiState.copy(publishDate = it) }
                            }
                        }
                    }

                    item {
                        if (isEditMode) {
                            EditableField("Price", uiState.price.toString(), true) {
                                uiState = uiState.copy(price = it.toDoubleOrNull() ?: 0.0)
                            }
                        } else {
                            Surface(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                            ) {
                                Text(
                                    text = "Price: ${uiState.price} ${uiState.currency}",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.padding(16.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }

                // --- Buttons ---
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (isEditMode) {
                        Button(
                            onClick = { onSaveClick(uiState) },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                        ) {
                            Text("Save Changes")
                        }
                        OutlinedButton(
                            onClick = {
                                uiState = initialUiState
                                onCancelEdit()
                            },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Cancel")
                        }
                    } else {
                        Button(
                            onClick = onEditClick,
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Edit")
                        }
                        Button(
                            onClick = { onDelete(bookId) },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(12.dp),
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
    singleLine: Boolean = true,
    onValueChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        if(isEditMode) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                label = { Text(label) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.bodyMedium,
                singleLine = singleLine,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
                )
            )
        } else {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = if (value.isBlank()) "N/A" else value,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                modifier = Modifier.padding(top = 2.dp, bottom = 6.dp)
            )
        }
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
            placeholder = { Text("https://...", fontSize = 12.sp) },
            singleLine = true,
            shape = RoundedCornerShape(8.dp)
        )
        IconButton(onClick = onDelete) {
            Icon(Icons.Default.Delete, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error)
        }
    }
}