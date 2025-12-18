package com.example.bookappdemo.ui.mapper

import com.example.bookappdemo.data.model.Book
import com.example.bookappdemo.data.model.BookData

fun Book.toUi(): BookData =
    BookData(
        id = id.toHexString(),
        title = title,
        author = author?.fullName.orEmpty(),
        rating = detail?.rating ?: 0.0,
        image = detail?.images?.firstOrNull()?.url.orEmpty()
    )
