package com.example.bookappdemo.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bookappdemo.data.repository.FirestoreRepository
import com.example.bookappdemo.ui.listBook.FirestoreViewModel


class FirestoreViewModelFactory(
    private val repository: FirestoreRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FirestoreViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FirestoreViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}