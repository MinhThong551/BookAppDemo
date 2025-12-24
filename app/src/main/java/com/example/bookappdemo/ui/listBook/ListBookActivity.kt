@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.bookappdemo.ui.listBook

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import com.example.bookappdemo.MyApp
import com.example.bookappdemo.data.repository.BookRepository


import com.example.bookappdemo.ui.base.ListBookViewModelFactory
import com.example.bookappdemo.ui.components.BottomNavItem

class ListBookActivity : ComponentActivity() {

    private lateinit var viewModel: ListBookViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = BookRepository(MyApp.realm)

        viewModel = ViewModelProvider(this,
            ListBookViewModelFactory(repository)
        )[ListBookViewModel::class.java]

        setContent {
            MainAppContent(viewModel = viewModel)
        }
    }
}
@Composable
fun MainAppContent(viewModel: ListBookViewModel) {
    var currentScreen by rememberSaveable{ mutableStateOf(BottomNavItem.Home) }
    val isLoading by viewModel.isLoading.collectAsState()
    val books by remember(viewModel) { viewModel.books }
        .collectAsState(initial = emptyList())
    val selectedUiState by viewModel.selectedBookUiState.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()

    val context = LocalContext.current

    LaunchedEffect(toastMessage) {
        toastMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.onToastShow()
        }
    }
    when (currentScreen) {
        BottomNavItem.Home -> {
            ListBookScreen(
                books = books,
                selectedUiState = selectedUiState,
                isLoading = isLoading,
                onNavigateToAdd = { currentScreen = BottomNavItem.Add },
                onBookClick = { viewModel.onBookClick(it) },
                onDismissDetail = { viewModel.dismissDetail() },
                onDeleteBook = { viewModel.deleteBook(it) },
                onSaveEdit = { viewModel.saveEdit(it) },
                onSearchOnline = { query ->
                    viewModel.searchOnline(query)
                }
            )
        }
        BottomNavItem.Add -> {
            AddBookScreen(
                onNavigateToHome = { currentScreen = BottomNavItem.Home },

                        onSaveClick = { title, author ->
                    viewModel.addSimpleBook(title, author)
                },
            )
        }
    }
}