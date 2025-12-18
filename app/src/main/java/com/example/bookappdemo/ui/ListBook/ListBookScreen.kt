package com.example.bookappdemo.ui.ListBook

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.bookappdemo.data.model.BookDetail
import com.example.bookappdemo.ui.base.BookDetailUiState
import com.example.bookappdemo.ui.base.toUiState

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
    val context = LocalContext.current
    val books by viewModel.books.collectAsState()

    val filteredBooks = books.filter {
        it.title.contains(searchQuery, ignoreCase = true) ||
                it.author.contains(searchQuery, ignoreCase = true)
    }
    val selectedDetail = viewModel.selectedBookDetail
    val bookId = viewModel.selectedBookId // Lấy ID đang được lưu trong ViewModel

    if (selectedDetail != null && viewModel.selectedBookId != null) {
        BookDetailDialog(
            bookId = viewModel.selectedBookId!!,
            detail = selectedDetail,
            isEditMode = viewModel.isEditMode,
            onDismiss = { viewModel.dismissDetail() },
            onDelete = { id ->
                viewModel.deleteBook(id)
                viewModel.dismissDetail()
                Toast.makeText(context, "Đã xóa sách thành công!", Toast.LENGTH_SHORT).show()
            },
            onEditClick = { viewModel.enableEdit() },
            onSaveClick = { updated ->
                viewModel.saveEdit(updated)
                Toast.makeText(context, "Đã cập nhật thông tin!", Toast.LENGTH_SHORT).show()
            },
            onCancelEdit = { viewModel.cancelEdit() }
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
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun BookDetailDialog(
    bookId: String,
    detail: BookDetail,
    isEditMode: Boolean,
    onDismiss: () -> Unit,
    onDelete: (String) -> Unit,
    onEditClick: () -> Unit,
    onSaveClick: (BookDetailUiState) -> Unit,
    onCancelEdit: () -> Unit
) {
    var uiState by remember(detail, isEditMode) {
        mutableStateOf(detail.toUiState())
    }

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
                    text = if (isEditMode) "✏️ Chỉnh sửa thông tin" else "📚 Chi tiết sách",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (detail.images.isNotEmpty()) {
                        item {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(detail.images) { image ->
                                    AsyncImage(
                                        model = image.url,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .width(200.dp)
                                            .height(300.dp)
                                            .clip(RoundedCornerShape(12.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
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
                                text = "⭐ ${detail.rating} (${detail.ratingCount} reviews)",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                    item {
                        EditableField("Mô tả", uiState.description, isEditMode) {
                            uiState = uiState.copy(description = it)
                        }
                        EditableField("Tóm tắt (Summary)", uiState.summary, isEditMode) {
                            uiState = uiState.copy(summary = it)
                        }
                    }

                    item {
                        EditableField("Ngôn ngữ", uiState.language, isEditMode) {
                            uiState = uiState.copy(language = it)
                        }
                        EditableField("Nhà xuất bản", uiState.publisher, isEditMode) {
                            uiState = uiState.copy(publisher = it)
                        }
                        EditableField("Ngày xuất bản", uiState.publishDate, isEditMode) {
                            uiState = uiState.copy(publishDate = it)
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(Modifier.weight(1f)) {
                                EditableField("Số trang", uiState.pages.toString(), isEditMode) {
                                    uiState = uiState.copy(pages = it.toIntOrNull() ?: 0)
                                }
                            }
                            Box(Modifier.weight(1f)) {
                                EditableField("Giá", uiState.price.toString(), isEditMode) {
                                    uiState = uiState.copy(price = it.toDoubleOrNull() ?: 0.0)
                                }
                            }
                        }
                    }
                    if (detail.categories.isNotEmpty() && !isEditMode) {
                        item {
                            Text(text = "Categories", style = MaterialTheme.typography.titleSmall)
                            FlowRow(modifier = Modifier.padding(top = 4.dp)) {
                                detail.categories.forEach { category ->
                                    Chip(category)
                                }
                            }
                        }
                    }
                    if (!isEditMode) {
                        item {
                            Text(
                                text = "Price: ${detail.price} ${detail.currency}",
                                style = MaterialTheme.typography.titleMedium,
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
                            onClick = onCancelEdit,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Hủy")
                        }
                    } else {
                        Button(
                            onClick = onEditClick,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Sửa")
                        }
                        Button(
                            onClick = { onDelete(bookId) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Xoá")
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
                color = Color.Gray,
                fontStyle = FontStyle.Italic,
                modifier = Modifier.padding(vertical = 2.dp)
            )
            Divider(thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.5f))
        }
    }
}
//@Composable
//fun InfoRow(label: String, value: String) {
//    if (value.isNotBlank()) {
//        Column {
//            Text(label, style = MaterialTheme.typography.titleMedium, color = Color.Black ,fontWeight = FontWeight.Bold)
//            Text(value, style = MaterialTheme.typography.bodyLarge , color = Color.Gray , fontStyle = FontStyle.Italic)
//            Spacer(Modifier.height(8.dp))
//        }
//    }
//}

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
//@Composable
//fun EditableText(
//    label: String,
//    value: String,
//    enabled: Boolean,
//    onChange: (String) -> Unit
//) {
//    Column {
//        Text(label, fontWeight = FontWeight.Bold)
//        if (enabled) {
//            TextField(value = value, onValueChange = onChange)
//        } else {
//            Text(value)
//        }
//    }
//}
//
//@Composable
//fun EditableNumber(
//    label: String,
//    value: Int,
//    enabled: Boolean,
//    onChange: (Int) -> Unit
//) {
//    Column {
//        Text(label, fontWeight = FontWeight.Bold)
//        if (enabled) {
//            TextField(
//                value = value.toString(),
//                onValueChange = { it.toIntOrNull()?.let(onChange) }
//            )
//        } else {
//            Text(value.toString())
//        }
//    }
//}
