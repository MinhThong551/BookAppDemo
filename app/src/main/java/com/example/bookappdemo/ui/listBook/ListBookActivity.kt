@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.bookappdemo.ui.listBook

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import com.example.bookappdemo.MyApp
import com.example.bookappdemo.data.repository.BookRepository
import com.example.bookappdemo.data.repository.FireStoreRepository
import com.example.bookappdemo.ui.components.BottomNavItem
import javax.inject.Inject

class ListBookActivity : ComponentActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var listViewModel: ListBookViewModel
    private lateinit var firestoreViewModel: FirestoreViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as MyApp).appComponent.inject(this)
        super.onCreate(savedInstanceState)

        listViewModel = ViewModelProvider(this, viewModelFactory)[ListBookViewModel::class.java]
        firestoreViewModel = ViewModelProvider(this, viewModelFactory)[FirestoreViewModel::class.java]
        setContent {
            MainAppContent(
                listViewModel = listViewModel,
                firestoreViewModel = firestoreViewModel
            )
        }
    }
}

@Composable
fun MainAppContent(
    listViewModel: ListBookViewModel,
    firestoreViewModel: FirestoreViewModel
) {
    var currentScreen by rememberSaveable { mutableStateOf(BottomNavItem.Home) }
    val context = LocalContext.current

    val homeIsLoading by listViewModel.isLoading.collectAsState()
    val homeBooks by remember(listViewModel) { listViewModel.books }
        .collectAsState(initial = emptyList())
    val homeSelectedUiState by listViewModel.selectedBookUiState.collectAsState()
    val homeToast by listViewModel.toastMessage.collectAsState()

    val fireIsLoading by firestoreViewModel.isLoading.collectAsState()
    val fireBooks by remember(firestoreViewModel) { firestoreViewModel.books }
        .collectAsState(initial = emptyList())
    val fireSelectedUiState by firestoreViewModel.selectedBookUiState.collectAsState()
    val fireToast by firestoreViewModel.toastMessage.collectAsState()


    LaunchedEffect(homeToast, fireToast) {
        homeToast?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            listViewModel.onToastShow()
        }
        fireToast?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            firestoreViewModel.onToastShow()
        }
    }


    when (currentScreen) {
        BottomNavItem.Home -> {
            ListBookScreen(
                books = homeBooks,
                selectedUiState = homeSelectedUiState,
                isLoading = homeIsLoading,
                onNavigateToAdd = { currentScreen = BottomNavItem.Add },
                onNavigateToFirestore = { currentScreen = BottomNavItem.FireStore },
                onBookClick = { listViewModel.onBookClick(it) },
                onDismissDetail = { listViewModel.dismissDetail() },
                onDeleteBook = { listViewModel.deleteBook(it) },
                onSaveEdit = { listViewModel.saveEdit(it) },
                onSearchOnline = { query -> listViewModel.searchOnline(query) }
            )
        }

        BottomNavItem.Add -> {
            AddBookScreen(
                onNavigateToHome = { currentScreen = BottomNavItem.Home },
                onNavigateToFirestore = { currentScreen = BottomNavItem.FireStore },
                onSaveClick = { title, author ->
                    listViewModel.addSimpleBook(title, author)

                },
            )
        }

        BottomNavItem.FireStore -> {
            ListBookFirestoreScreen(
                books = fireBooks,
                selectedUiState = fireSelectedUiState,
                isLoading = fireIsLoading,

                onNavigateHome = { currentScreen = BottomNavItem.Home },
                onNavigateToAdd = { currentScreen = BottomNavItem.Add },

                onBookClick = { firestoreViewModel.onBookClick(it) },
                onDismissDetail = { firestoreViewModel.dismissDetail() },
                onSearchLocal = {}
            )
        }
    }
}