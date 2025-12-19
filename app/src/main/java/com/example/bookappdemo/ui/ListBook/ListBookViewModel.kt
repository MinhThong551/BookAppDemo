package com.example.bookappdemo.ui.ListBook

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookappdemo.data.model.Author
import com.example.bookappdemo.data.model.BookData
import com.example.bookappdemo.data.model.BookDetail
import com.example.bookappdemo.data.repository.BookRepository
import com.example.bookappdemo.ui.base.BookDetailUiState
import com.example.bookappdemo.ui.base.toUiState
import com.example.bookappdemo.ui.mapper.toUi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ListBookViewModel(
    private val repository: BookRepository
) : ViewModel() {

    var selectedBookId: String? = null
        private set
    private val _selectedBookUiState = mutableStateOf<BookDetailUiState?>(null)
    val selectedBookUiState: BookDetailUiState?
        get() = _selectedBookUiState.value
    fun saveEdit(updated: BookDetailUiState) {
        val bookId = selectedBookId ?: return
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateBookFull(bookId, updated)
        }
            onBookClick(bookId)
        }

    val books = repository.observeBooks()
        .map { list -> list.map { it.toUi() } }
        .flowOn(Dispatchers.Default)
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            emptyList()
        )

    init {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertSampleData()
        }
    }

    fun onBookClick(bookId: String) {
        selectedBookId = bookId
        viewModelScope.launch(Dispatchers.IO) {
            val book = repository.getBookById(bookId)
            _selectedBookUiState.value = book?.toUiState()        }
    }

    fun dismissDetail() {
        _selectedBookUiState.value = null
    }

    fun deleteBook(bookId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteBook(bookId)
            dismissDetail()
        }
    }


//    fun updateBook(bookId: String, update: BookDetail.() -> Unit) {
//         viewModelScope.launch(Dispatchers.IO) {
//            repository.updateBook(bookId, update)
//        }
//    }
//
//    fun addBook(author: Author, detail: BookDetail) {
//        viewModelScope.launch(Dispatchers.IO){
//            repository.addBook(
//                title = detail.summary.take(30),
//                author = author,
//                detail = detail
//            )
//        }
//    }
    fun addSimpleBook(title: String, authorName: String) {
        viewModelScope.launch(Dispatchers.IO){
            repository.addSimpleBook(title, authorName)
        }
    }
}
