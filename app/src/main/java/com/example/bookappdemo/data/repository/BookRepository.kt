package com.example.bookappdemo.data.repository

import BookApiService
import com.example.bookappdemo.data.model.Author
import com.example.bookappdemo.data.model.Book
import com.example.bookappdemo.data.model.BookDetail
import com.example.bookappdemo.data.model.ImageInfo
import com.example.bookappdemo.data.api.RetrofitClient
import com.example.bookappdemo.data.network.NetworkBook
import com.example.bookappdemo.ui.base.BookDetailUiState
import com.example.bookappdemo.utils.Resource
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class BookRepository(
    private val realm: Realm,
    private val apiService: BookApiService = RetrofitClient.instance) {

    fun observeBooks(): Flow<List<Book>> =
        realm.query<Book>()
            .asFlow()
            .map { it.list.toList() }
            .flowOn(Dispatchers.IO)

    suspend fun getBookById(id: String): Book? = withContext(Dispatchers.IO) {
        realm.query<Book>(
            "id == $0",
            org.mongodb.kbson.ObjectId(id)
        ).first().find()
    }
    private fun findLocalBook(localId: String): Book? {
        return try {
            realm.query<Book>(
                "id == $0",
                org.mongodb.kbson.ObjectId(localId)
            ).first().find()
        } catch (e: Exception) {
            null
        }
    }
    suspend fun addSimpleBook(title: String, authorName: String): Resource<Boolean> {
       return withContext(Dispatchers.IO) {
            try {

                val newNetBook = NetworkBook(
                    id = "",
                    title = title,
                    authorName = authorName,
                    description = "no description",
                    summary = "",
                    price = 0.0,
                    currency = "USD",
                    rating = 0.0,
                    ratingCount = 0,
                    pages = 0,
                    language = "vi",
                    publisher = "",
                    publishDate = "",
                    images = ""
                )
                val responseBook = apiService.addBook(newNetBook)
                saveBooksToDatabase(listOf(responseBook))
                Resource.Success(true)
            } catch (e: Exception) {
                e.printStackTrace()
                Resource.Error(e.message ?: "ADD ERROR")
            }
        }
    }


    suspend fun updateBookFull(localId: String, uiState: BookDetailUiState) : Resource<Boolean>{
       return withContext(Dispatchers.IO) {
            try {
                val localBook = findLocalBook(localId)
                if (localBook == null || localBook.remoteId.isBlank()) {
                    return@withContext Resource.Error("BOOK IS NOT EXISTED")

                }
                val apiId = localBook.remoteId
                val updateNetBook = NetworkBook(
                    id = apiId,
                    title = uiState.title,
                    authorName = uiState.authorName,
                    description = uiState.description,
                    summary = uiState.summary,
                    price = uiState.price,
                    currency = uiState.currency,
                    rating = uiState.rating,
                    ratingCount = uiState.ratingCount,
                    pages = uiState.pages,
                    language = uiState.language,
                    publisher = uiState.publisher,
                    publishDate = uiState.publishDate,
                    images = uiState.images.firstOrNull() ?: ""
                )

                val responseBook = apiService.updateBook(apiId, updateNetBook)
                saveBooksToDatabase(listOf(responseBook))
                Resource.Success(true)
            } catch (e: Exception) {
                e.printStackTrace()
                Resource.Error(e.message ?: "UPDATE ERROR")
            }
        }
    }

    suspend fun deleteBook(localId: String): Resource<Boolean> {
       return withContext(Dispatchers.IO) {
            try {
                val localBook = findLocalBook(localId) ?: return@withContext Resource.Error("ID NOT FOUND")
                val apiId = localBook.remoteId

                if (apiId.isNotBlank()) {
                    apiService.deleteBook(apiId)
                }
                realm.write {
                    findLatest(localBook)?.let { delete(it) }
                }
                Resource.Success(true)
            } catch (e: Exception) {
                e.printStackTrace()
                Resource.Error(e.message ?: "DELETE ERROR")
            }
        }
    }
    suspend fun syncDefaultBooks() {
        withContext(Dispatchers.IO) {
            try {
                val networkBooks = apiService.getRecommendedBooks()
                saveBooksToDatabase(networkBooks)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Hàm tìm kiếm sách
    suspend fun searchAndSyncBooks(query: String) {
        withContext(Dispatchers.IO) {
            try {
                val networkBooks = apiService.searchBooks(query)
                saveBooksToDatabase(networkBooks)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    private suspend fun saveBooksToDatabase(networkBooks: List<NetworkBook>) {
        realm.write {
            for (netBook in networkBooks) {
                val authorName = netBook.authorName
                var authorObj = query<Author>("fullName == $0", authorName).first().find()

                if (authorObj == null) {
                    authorObj = copyToRealm(Author().apply { fullName = authorName })
                }

                val detailObj = BookDetail().apply {
                    description = netBook.description ?: ""
                    summary = netBook.summary ?: (netBook.description ?: "")

                    price = netBook.price ?: 0.0
                    currency = netBook.currency ?: "USD"

                    rating = netBook.rating ?: 0.0
                    ratingCount = netBook.ratingCount ?: 0
                    pages = netBook.pages ?: 0

                    language = netBook.language ?: "en"
                    publisher = netBook.publisher ?: ""
                    publishDate = netBook.publishDate ?: ""

                    if (!netBook.images.isNullOrEmpty()) {
                        images.add(ImageInfo().apply {
                            url = netBook.images
                            type = "cover"
                        })
                    }
                }

                // Lưu Book
                val existingBook = query<Book>("remoteId== $0", netBook.id).first().find()
                if (existingBook == null) {
                    val newBook = Book().apply {
                        this.id = org.mongodb.kbson.ObjectId()
                        this.remoteId = netBook.id
                        this.title = netBook.title
                        this.author = authorObj
                        this.detail = detailObj
                    }
                    val savedBook = copyToRealm(newBook)
                    authorObj.books.add(savedBook)
                } else {
                    existingBook.title = netBook.title
                    existingBook.detail = detailObj
                    existingBook.author = authorObj
                }
                }
            }
        }
    }