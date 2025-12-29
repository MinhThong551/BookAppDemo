package com.example.bookappdemo.ui.mapper

import com.example.bookappdemo.ui.base.BookDetailUiState
import com.google.firebase.firestore.DocumentSnapshot

fun DocumentSnapshot.toUiState(): BookDetailUiState {
    return BookDetailUiState(
        id = this.id,
        title = this.getString("title") ?: "",
        authorName = this.getString("authorName") ?: "",
        images = (this.get("images") as? List<String>) ?: emptyList(),
        description = this.getString("description") ?: "",
        summary = this.getString("summary") ?: "",
        language = this.getString("language") ?: "",
        publisher = this.getString("publisher") ?: "",
        publishDate = this.getString("publishDate") ?: "",
        pages = (this.get("pages") as? Number)?.toInt() ?: 0,
        rating = (this.get("rating") as? Number)?.toDouble() ?: 0.0,
        ratingCount = (this.get("ratingCount") as? Number)?.toInt() ?: 0,
        price = (this.get("price") as? Number)?.toDouble() ?: 0.0,
        currency = this.getString("currency") ?: ""
    )
}