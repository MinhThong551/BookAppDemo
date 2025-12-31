package com.example.bookappdemo.ui.listBook

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookappdemo.data.model.BookData
import com.example.bookappdemo.data.repository.BookRepository
import com.example.bookappdemo.ui.base.BookDetailUiState
import com.example.bookappdemo.ui.base.toBookData
import com.example.bookappdemo.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class ListBookViewModel @Inject constructor(
    private val repository: BookRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage = _toastMessage.asStateFlow()

    private val _selectedBookUiState = MutableStateFlow<BookDetailUiState?>(null)
    val selectedBookUiState = _selectedBookUiState.asStateFlow()

    private val _books = MutableStateFlow<List<BookData>>(emptyList())
    val books = _books.asStateFlow()

    private var cachedUiStates = mutableListOf<BookDetailUiState>()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            when (val result = repository.getAllBooks()) {
                is Resource.Success -> {
                    cachedUiStates = result.data?.toMutableList() ?: mutableListOf()
                    updateBookDataList()
                }
                is Resource.Error -> {
                    _toastMessage.value = result.message
                }
                else -> {}
            }
            _isLoading.value = false
        }
    }

    private fun updateBookDataList() {
        _books.value = cachedUiStates.map { it.toBookData() }
    }

    fun onBookClick(bookId: String) {
        val book = cachedUiStates.find { it.id == bookId }
        _selectedBookUiState.value = book
    }

    fun dismissDetail() {
        _selectedBookUiState.value = null
    }

    fun deleteBook(bookId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            when (repository.deleteBook(bookId)) {
                is Resource.Success -> {
                    _toastMessage.value = "DELETE SUCCESS"
                    cachedUiStates.removeAll { it.id == bookId }
                    updateBookDataList()
                    dismissDetail()
                }
                is Resource.Error -> _toastMessage.value = "DELETE FAILED"
                else -> {}
            }
            _isLoading.value = false
        }
    }

    fun addSimpleBook(title: String, authorName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            when (val result = repository.addSimpleBook(title, authorName)) {
                is Resource.Success -> {
                    _toastMessage.value = "ADD SUCCESS"
                    result.data?.let { newItem ->
                        cachedUiStates.add(0, newItem) // Thêm lên đầu
                        updateBookDataList()
                    }
                }
                is Resource.Error -> _toastMessage.value = "ADD FAILED"
                else -> {}
            }
            _isLoading.value = false
        }
    }

    fun saveEdit(updated: BookDetailUiState) {
        viewModelScope.launch {
            _isLoading.value = true
            when (repository.updateBookFull(updated.id, updated)) {
                is Resource.Success -> {
                    _toastMessage.value = "UPDATE SUCCESS"
                    // Cập nhật list RAM
                    val index = cachedUiStates.indexOfFirst { it.id == updated.id }
                    if (index != -1) {
                        cachedUiStates[index] = updated
                        updateBookDataList()
                    }
                    _selectedBookUiState.value = updated // Cập nhật luôn dialog
                }
                is Resource.Error -> _toastMessage.value = "UPDATE FAILED"
                else -> {}
            }
            _isLoading.value = false
        }
    }

    fun searchOnline(query: String) {
        if(query.isBlank()) {
            loadData()
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            when (val result = repository.searchBooks(query)) {
                is Resource.Success -> {
                    cachedUiStates = result.data?.toMutableList() ?: mutableListOf()
                    updateBookDataList()
                }
                is Resource.Error -> _toastMessage.value = "Search Failed"
                else -> {}
            }
            _isLoading.value = false
        }
    }

    fun onToastShow() { _toastMessage.value = null }



}