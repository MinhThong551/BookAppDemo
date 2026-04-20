package com.example.bookappdemo.ui.listBook

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookappdemo.data.repository.FireStoreRepository
import com.example.bookappdemo.ui.base.BookDetailUiState
import com.example.bookappdemo.ui.base.toUiState
import com.example.bookappdemo.ui.mapper.toUi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class FirestoreViewModel @Inject constructor(
    private val repository: FireStoreRepository
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage = _toastMessage.asStateFlow()
    private val _selectedBookUiState = MutableStateFlow<BookDetailUiState?>(null)
    val selectedBookUiState = _selectedBookUiState.asStateFlow()
    private var detailJob : Job? = null
    private val _isPaginating = MutableStateFlow(false)
    val isPaginating = _isPaginating.asStateFlow()

    val books = repository.observeBooks()
        .map { list -> list.map { it.toUi() } }
        .flowOn(Dispatchers.Default)

    init {
        refresh()
    }
    fun refresh() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.refreshData()
            _isLoading.value = false
        }
    }
    fun loadNextPage() {
        if (_isPaginating.value || repository.isLastPage) return

        viewModelScope.launch {
            _isPaginating.value = true
            repository.loadNextPage()
            _isPaginating.value = false
        }
    }

    fun onToastShow() { _toastMessage.value = null }

    fun onBookClick(bookId: String) {
        detailJob?.cancel()

        detailJob = viewModelScope.launch(Dispatchers.IO) {
            repository.observeBookById(bookId).collect { book ->
                _selectedBookUiState.value = book?.toUiState()
            }
        }
    }

    fun dismissDetail() {
        _selectedBookUiState.value = null
        detailJob?.cancel()

    }

    override fun onCleared() {
        super.onCleared()
        repository.stopRealtimeSync()
    }
}