package com.example.bookappdemo.ui.base

import com.example.bookappdemo.data.model.BookDetail

data class BookDetailUiState(
    var description: String,
    var summary: String,
    var language: String,
    var publisher: String,
    var publishDate: String,
    var pages: Int,
    var rating: Double,
    var ratingCount: Int,
    var price: Double,
    var currency: String
)

fun BookDetail.toUiState() = BookDetailUiState(
    description,
    summary,
    language,
    publisher,
    publishDate,
    pages,
    rating,
    ratingCount,
    price,
    currency
)
