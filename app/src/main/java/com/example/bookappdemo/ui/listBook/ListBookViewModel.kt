package com.example.bookappdemo.ui.listBook

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookappdemo.data.repository.BookRepository
import com.example.bookappdemo.ui.base.BookDetailUiState
import com.example.bookappdemo.ui.base.toUiState
import com.example.bookappdemo.ui.mapper.toUi
import com.example.bookappdemo.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ListBookViewModel(
    private val repository: BookRepository
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _toastMessage = Channel<String>()
    val toastMessage = _toastMessage.receiveAsFlow()
    private val _selectedBookUiState = mutableStateOf<BookDetailUiState?>(null)
    val selectedBookUiState: BookDetailUiState?
        get() = _selectedBookUiState.value

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
            _isLoading.value = true
            repository.syncDefaultBooks()
            _isLoading.value = false
        }
    }

    fun onBookClick(bookId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val book = repository.getBookById(bookId)
            _selectedBookUiState.value = book?.toUiState()
        }
    }

    fun dismissDetail() {
        _selectedBookUiState.value = null
    }

    fun deleteBook(bookId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true

            val result = repository.deleteBook(bookId)

            when (result) {
                is Resource.Success -> {
                    _toastMessage.send("DELETE SUCCESS")
                    dismissDetail() // Đóng popup chi tiết
                }
                is Resource.Error -> {
                    val msg = result.message ?: "DELETE FAIL"
                    _toastMessage.send(msg)
                }
                else -> {}
            }
            _isLoading.value = false
        }
    }

    fun addSimpleBook(title: String, authorName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true

            val result = repository.addSimpleBook(title, authorName)

            when (result) {
                is Resource.Success -> {
                    _toastMessage.send("ADD BOOK SUCCESS")
                }
                is Resource.Error -> {
                    val msg = result.message ?: "ADD BOOK FAIL"
                    _toastMessage.send(msg)
                }
                else -> {}
            }
            _isLoading.value = false
        }
    }

    fun searchOnline(query: String) {
        if (query.isBlank()) return

        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            try {
                repository.searchAndSyncBooks(query)
            } catch (e: Exception) {
                e.printStackTrace()
                _toastMessage.send("SEARCH FAIL: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun saveEdit(updated: BookDetailUiState) {
        if (updated.id.isBlank()) return

        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true

            val result = repository.updateBookFull(updated.id, updated)

            when (result) {
                is Resource.Success -> {
                    _toastMessage.send("UPDATE SUCCESS")
                        onBookClick(updated.id)
                }
                is Resource.Error -> {
                    val msg = result.message ?: "UPDATE FAIL"
                    _toastMessage.send(msg)
                }
                else -> {}
            }
            _isLoading.value = false
        }
    }
}
