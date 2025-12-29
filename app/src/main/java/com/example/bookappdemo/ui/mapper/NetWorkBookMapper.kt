package com.example.bookappdemo.ui.mapper

import com.example.bookappdemo.data.model.BookDetail
import com.example.bookappdemo.data.model.ImageInfo
import com.example.bookappdemo.data.network.NetworkBook
import com.example.bookappdemo.ui.base.BookDetailUiState

fun NetworkBook.toUiState(): BookDetailUiState {
    return BookDetailUiState(
        id = this.id,
        title = this.title,
        authorName = this.authorName,
        description = this.description ?: "",
        summary = this.summary ?: "",
        price = this.price ?: 0.0,
        currency = this.currency ?: "USD",
        rating = this.rating ?: 0.0,
        ratingCount = this.ratingCount ?: 0,
        pages = this.pages ?: 0,
        language = this.language ?: "en",
        publisher = this.publisher ?: "",
        publishDate = this.publishDate ?: "",
        images = this.images ?: emptyList()
    )
}