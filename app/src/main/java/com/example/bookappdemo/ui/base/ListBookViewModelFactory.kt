package com.example.bookappdemo.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bookappdemo.data.repository.BookRepository
import com.example.bookappdemo.ui.ListBook.ListBookViewModel

class ListBookViewModelFactory(
    private val repository: BookRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListBookViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ListBookViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
