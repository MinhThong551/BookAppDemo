package com.example.bookappdemo.ui.base

import android.os.Parcelable
import com.example.bookappdemo.data.model.Book
import com.example.bookappdemo.data.model.BookData
import com.example.bookappdemo.data.model.BookDetail
import com.example.bookappdemo.data.network.NetworkBook
import kotlinx.android.parcel.Parcelize

@Suppress("DEPRECATED_ANNOTATION")
@Parcelize
data class BookDetailUiState(
    val id: String = "",
    val title: String = "",
    val authorName: String = "",
    val images: List<String> = emptyList(),
    val description: String = "",
    val summary: String = "",
    val language: String = "",
    val publisher: String = "",
    val publishDate: String = "",
    val pages: Int = 0,
    val rating: Double = 0.0,
    val ratingCount: Int = 0,
    val price: Double = 0.0,
    val currency: String = ""
) : Parcelable

fun Book.toUiState(): BookDetailUiState {
    return BookDetailUiState(
        id = this.id.toHexString(),
        title = this.title,
        authorName = this.author?.fullName ?: "",

        images = this.detail?.images?.map { it.url } ?: emptyList(),
        description = this.detail?.description ?: "",
        summary = this.detail?.summary ?: "",
        language = this.detail?.language ?: "en",
        publisher = this.detail?.publisher ?: "",
        publishDate = this.detail?.publishDate ?: "",
        pages = this.detail?.pages ?: 0,
        rating = this.detail?.rating ?: 0.0,
        ratingCount = this.detail?.ratingCount ?: 0,
        price = this.detail?.price ?: 0.0,
        currency = this.detail?.currency ?: "USD"
    )
}
fun BookDetail.toUiState(
    title: String = "",
    authorName: String = ""
) = BookDetailUiState(
    title = title,
    authorName = authorName,
    images = this.images.map { it.url },
    description = this.description,
    summary = this.summary,
    language = this.language,
    publisher = this.publisher,
    publishDate = this.publishDate,
    pages = this.pages,
    rating = this.rating,
    ratingCount = this.ratingCount,
    price = this.price,
    currency = this.currency
)
fun BookDetailUiState.toNetworkBook(): NetworkBook {
    return NetworkBook(
        id = "",
        title = this.title,
        authorName = this.authorName,
        description = this.description,
        summary = this.summary,
        price = this.price,
        currency = this.currency,
        rating = this.rating,
        ratingCount = this.ratingCount,
        pages = this.pages,
        language = this.language,
        publisher = this.publisher,
        publishDate = this.publishDate,
        images = this.images
    )

}
fun BookDetailUiState.toBookData(): BookData {
    return BookData(
        id = this.id,
        title = this.title,
        author = this.authorName,
        rating = this.rating,
        image = this.images.firstOrNull() ?: ""
    )
}