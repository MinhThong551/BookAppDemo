package com.example.bookappdemo.data.network

import com.example.bookappdemo.data.model.BookDetail
import com.example.bookappdemo.data.model.ImageInfo
import com.google.gson.annotations.SerializedName

data class NetworkBook(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("authorName") val authorName: String,
    @SerializedName("description") val description: String?,
    @SerializedName("summary") val summary: String?,
    @SerializedName("price") val price: Double?,
    @SerializedName("currency") val currency: String?,
    @SerializedName("rating") val rating: Double?,
    @SerializedName("ratingCount") val ratingCount: Int?,
    @SerializedName("pages") val pages: Int?,
    @SerializedName("language") val language: String?,
    @SerializedName("publisher") val publisher: String?,
    @SerializedName("publishDate") val publishDate: String?,
    @SerializedName("images") val images: String?
)
fun NetworkBook.toBookDetail(): BookDetail {
    return BookDetail().apply {
        description = this@toBookDetail.description ?: ""
        summary = this@toBookDetail.summary ?: (this@toBookDetail.description ?: "")

        price = this@toBookDetail.price ?: 0.0
        currency = this@toBookDetail.currency ?: "USD"

        rating = this@toBookDetail.rating ?: 0.0
        ratingCount = this@toBookDetail.ratingCount ?: 0
        pages = this@toBookDetail.pages ?: 0

        language = this@toBookDetail.language ?: "en"
        publisher = this@toBookDetail.publisher ?: ""
        publishDate = this@toBookDetail.publishDate ?: ""

        if (!this@toBookDetail.images.isNullOrEmpty()) {
            images.add(ImageInfo().apply {
                url = this@toBookDetail.images
                type = "cover"
            })
        }
    }
}