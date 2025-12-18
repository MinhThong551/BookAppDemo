@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.bookappdemo.ui.ListBook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.lifecycle.ViewModelProvider
import com.example.bookappdemo.MyApp
import com.example.bookappdemo.data.repository.BookRepository
import com.example.bookappdemo.ui.base.ListBookViewModelFactory

class ListBookActivity : ComponentActivity() {

    private lateinit var viewModel: ListBookViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = BookRepository(MyApp.realm)

        viewModel = ViewModelProvider(
            this,
            ListBookViewModelFactory(repository)
        )[ListBookViewModel::class.java]
        setContent {
            ListBookScreen(viewModel = viewModel)
        }
    }
}