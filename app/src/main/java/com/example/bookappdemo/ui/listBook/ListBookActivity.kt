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
import com.example.bookappdemo.data.repository.FirestoreRepository
import com.example.bookappdemo.ui.base.FirestoreViewModelFactory
import com.example.bookappdemo.ui.base.ListBookViewModelFactory
import com.example.bookappdemo.ui.components.BottomNavItem

class ListBookActivity : ComponentActivity() {

    // Khai báo cả 2 ViewModel
    private lateinit var listViewModel: ListBookViewModel
    private lateinit var firestoreViewModel: FirestoreViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Khởi tạo Repository
        val bookRepository = BookRepository()
        val firestoreRepository = FirestoreRepository(MyApp.realm)

        // 2. Khởi tạo ListBookViewModel (Cho Home + Add)
        listViewModel = ViewModelProvider(
            this,
            ListBookViewModelFactory(bookRepository)
        )[ListBookViewModel::class.java]

        // 3. Khởi tạo FirestoreViewModel (Cho Firestore)
        // Việc tạo ở đây giúp ViewModel sống xuyên suốt, không bị load lại khi chuyển tab
        firestoreViewModel = ViewModelProvider(
            this,
            FirestoreViewModelFactory(firestoreRepository)
        )[FirestoreViewModel::class.java]

        setContent {
            // Truyền cả 2 ViewModel vào Content chính
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
    // Quản lý chuyển tab tại đây (Single Activity)
    var currentScreen by rememberSaveable { mutableStateOf(BottomNavItem.Home) }
    val context = LocalContext.current

    // --- State cho HOME ---
    val homeIsLoading by listViewModel.isLoading.collectAsState()
    val homeBooks by remember(listViewModel) { listViewModel.books }
        .collectAsState(initial = emptyList())
    val homeSelectedUiState by listViewModel.selectedBookUiState.collectAsState()
    val homeToast by listViewModel.toastMessage.collectAsState()

    // --- State cho FIRESTORE ---
    val fireIsLoading by firestoreViewModel.isLoading.collectAsState()
    val fireBooks by remember(firestoreViewModel) { firestoreViewModel.books }
        .collectAsState(initial = emptyList())
    val fireSelectedUiState by firestoreViewModel.selectedBookUiState.collectAsState()
    val fireToast by firestoreViewModel.toastMessage.collectAsState()


    // Xử lý Toast chung cho cả 2 ViewModel
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

    // Điều hướng nội dung dựa trên currentScreen
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
                onSearchLocal = { /* Logic search local nếu cần */ }
            )
        }
    }
}