package com.example.bookappdemo.ui.listBook

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookappdemo.data.repository.FirestoreRepository
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

class FirestoreViewModel(
    private val repository: FirestoreRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage = _toastMessage.asStateFlow()
    private val _selectedBookUiState = MutableStateFlow<BookDetailUiState?>(null)
    val selectedBookUiState = _selectedBookUiState.asStateFlow()
    private var detailJob : Job? = null

    val books = repository.observeBooks()
        .map { list -> list.map { it.toUi() } }
        .flowOn(Dispatchers.Default)

    init {

            loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            val success = repository.fetchAndSyncData()

            if (!success) {
                _toastMessage.value = "Failed to connect API / Sync"
            }
            _isLoading.value = false
        }
    }

    fun onToastShow() { _toastMessage.value = null }

    fun onBookClick(bookId: String) {
        // 1. Hủy việc lắng nghe cuốn sách cũ (nếu có) để tránh lỗi chồng chéo
        detailJob?.cancel()

        // 2. Bắt đầu lắng nghe cuốn sách mới
        detailJob = viewModelScope.launch(Dispatchers.IO) {
            repository.observeBookById(bookId).collect { book ->
                // Mỗi khi Realm cập nhật (do Firestore sync), dòng code này sẽ chạy lại
                // _selectedBookUiState sẽ có dữ liệu mới -> UI Dialog tự vẽ lại ngay lập tức
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