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
import com.example.bookappdemo.ui.mapper.toUi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ListBookViewModel(
    private val repository: BookRepository
) : ViewModel() {

    var selectedBookId: String? = null
        private set

    var isEditMode by mutableStateOf(false)
        private set

    fun enableEdit() {
        isEditMode = true
    }

    fun cancelEdit() {
        isEditMode = false
    }
    fun saveEdit(updated: BookDetailUiState) {
        val bookId = selectedBookId ?: return
        viewModelScope.launch {
            repository.updateBook(bookId) {
                description = updated.description
                summary = updated.summary
                language = updated.language
                publisher = updated.publisher
                publishDate = updated.publishDate
                pages = updated.pages
                rating = updated.rating
                ratingCount = updated.ratingCount
                price = updated.price
                currency = updated.currency
            }
            // Cập nhật lại UI sau khi lưu thành công
            onBookClick(bookId)
            isEditMode = false
        }
    }
    val books = repository.observeBooks()
        .map { list -> list.map { it.toUi() } }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            emptyList()
        )

    init {
        viewModelScope.launch {
            repository.insertSampleData()
        }
    }

    private val _selectedBookDetail = mutableStateOf<BookDetail?>(null)
    val selectedBookDetail: BookDetail?
        get() = _selectedBookDetail.value

    fun onBookClick(bookId: String) {
        selectedBookId = bookId
        val book = repository.getBookById(bookId)
        _selectedBookDetail.value = book?.detail
    }


    fun dismissDetail() {
        _selectedBookDetail.value = null
    }

    fun deleteBook(bookId: String) {
        viewModelScope.launch {
            repository.deleteBook(bookId)
        }
    }


    fun updateBook(bookId: String, update: BookDetail.() -> Unit) {
        viewModelScope.launch {
            repository.updateBook(bookId, update)
        }
    }

    fun addBook(author: Author, detail: BookDetail) {
        viewModelScope.launch {
            repository.addBook(
                title = detail.summary.take(30),
                author = author,
                detail = detail
            )
        }
    }
}
