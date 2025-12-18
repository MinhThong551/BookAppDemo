package com.example.bookappdemo.ui.ListBook

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookappdemo.data.model.BookData
import com.example.bookappdemo.data.model.BookDetail
import com.example.bookappdemo.data.repository.BookRepository
import com.example.bookappdemo.ui.mapper.toUi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ListBookViewModel(
    private val repository: BookRepository
) : ViewModel() {

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
        val book = repository.getBookById(bookId)
        _selectedBookDetail.value = book?.detail
    }

    fun dismissDetail() {
        _selectedBookDetail.value = null
    }
}
