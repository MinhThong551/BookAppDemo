package com.example.bookappdemo.data.network

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