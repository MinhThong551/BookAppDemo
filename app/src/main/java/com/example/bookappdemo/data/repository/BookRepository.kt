package com.example.bookappdemo.data.repository

import com.example.bookappdemo.data.api.BookApiService
import com.example.bookappdemo.data.model.Author
import com.example.bookappdemo.data.model.Book
import com.example.bookappdemo.data.model.BookDetail
import com.example.bookappdemo.data.model.ImageInfo
import com.example.bookappdemo.data.network.NetworkBook
import com.example.bookappdemo.ui.base.BookDetailUiState
import com.example.bookappdemo.ui.base.toNetworkBook
import com.example.bookappdemo.ui.mapper.toUiState
import com.example.bookappdemo.utils.Resource
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BookRepository @Inject constructor(
    private val apiService: BookApiService
) {

    suspend fun getAllBooks():Resource<List<BookDetailUiState>>{
        return withContext(Dispatchers.IO) {
        try {
            val response = apiService.getAllBooks()
            val books = response.map { it.toUiState() }
            Resource.Success(books)
        } catch (e :Exception){
            Resource.Error(e.message ?: "Fetch error")
        }
        }

    }


    suspend fun searchBooks(query: String): Resource<List<BookDetailUiState>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.searchBooks(query)
                Resource.Success(response.map { it.toUiState() })
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Search error")
            }
        }
    }

    suspend fun getBookById(id:String) : Resource<BookDetailUiState>{
    return withContext(Dispatchers.IO) {
        try {
            val response = apiService.getBookById(id)
            Resource.Success(response.toUiState())
        } catch (e : Exception){
            Resource.Error(e.message ?: "Book not found")
        }
    }
}
    suspend fun addSimpleBook(title: String, authorName: String): Resource<BookDetailUiState> {
        return withContext(Dispatchers.IO) {
            try {
                val newNetBook = NetworkBook(
                    id = "", title = title, authorName = authorName,
                    description = "Added via API", summary = "", price = 0.0,
                    currency = "USD", rating = 0.0, ratingCount = 0, pages = 0,
                    language = "en", publisher = "", publishDate = "",
                    images = emptyList()
                )
                val responseBook = apiService.addBook(newNetBook)
                Resource.Success(responseBook.toUiState()) // Trả về sách vừa tạo
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Add Error")
            }
        }
    }


    suspend fun updateBookFull(id: String, uiState: BookDetailUiState): Resource<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val updateNetBook = uiState.toNetworkBook()
                apiService.updateBook(id, updateNetBook)
                Resource.Success(true)
            } catch (e: Exception) {
                Resource.Error("Update Error")
            }
        }
    }

    suspend fun deleteBook(id: String): Resource<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                apiService.deleteBook(id)
                Resource.Success(true)
            } catch (e : Exception) {
                Resource.Error(e.message ?: "Delete Error")
            }

        }
    }


    }

//    fun observeBooks(): Flow<List<Book>> =
//        realm.query<Book>()
//            .asFlow()
//            .map { it.list.toList() }
//            .flowOn(Dispatchers.IO)
//
//    suspend fun getBookById(id: String): Book? = withContext(Dispatchers.IO) {
//        realm.query<Book>("id == $0", org.mongodb.kbson.ObjectId(id)).first().find()
//    }
//
//    private fun findLocalBook(localId: String): Book? {
//        return try {
//            realm.query<Book>("id == $0", org.mongodb.kbson.ObjectId(localId)).first().find()
//        } catch (e: Exception) { null }
//    }
//
//
//    suspend fun addSimpleBook(title: String, authorName: String): Resource<Boolean> {
//        return withContext(Dispatchers.IO) {
//            try {
//                // 1. Gọi API
//                val newNetBook = NetworkBook(
//                    id = "", title = title, authorName = authorName,
//                    description = "Standard API Flow", summary = "", price = 0.0,
//                    currency = "USD", rating = 0.0, ratingCount = 0, pages = 0,
//                    language = "en", publisher = "", publishDate = "",
//                    images = emptyList()
//                )
//                val responseBook = apiService.addBook(newNetBook)
//
//                // 2. API Success -> Lưu vào Realm
//                saveBooksToDatabase(listOf(responseBook.toUiState()))
//                Resource.Success(true)
//            } catch (e: Exception) {
//                e.printStackTrace()
//                Resource.Error(e.message ?: "ADD ERROR")
//            }
//        }
//    }
//
//    suspend fun updateBookFull(localId: String, uiState: BookDetailUiState): Resource<Boolean> {
//        return withContext(Dispatchers.IO) {
//            try {
//                val localBook = findLocalBook(localId) ?: return@withContext Resource.Error("Book not found")
//                val apiId = localBook.remoteId
//
//                val updateNetBook = NetworkBook(
//                    id = apiId, title = uiState.title, authorName = uiState.authorName,
//                    description = uiState.description, summary = uiState.summary,
//                    price = uiState.price, currency = uiState.currency,
//                    rating = uiState.rating, ratingCount = uiState.ratingCount,
//                    pages = uiState.pages, language = uiState.language,
//                    publisher = uiState.publisher, publishDate = uiState.publishDate,
//                    images = uiState.images
//                )
//                val responseBook = apiService.updateBook(apiId, updateNetBook)
//
//                saveBooksToDatabase(listOf(responseBook.toUiState()))
//                Resource.Success(true)
//            } catch (e: Exception) {
//                Resource.Error("UPDATE ERROR")
//            }
//        }
//    }
//
//    suspend fun deleteBook(localId: String): Resource<Boolean> {
//        return withContext(Dispatchers.IO) {
//            try {
//                val localBook = findLocalBook(localId) ?: return@withContext Resource.Error("ID NOT FOUND")
//
//                // 1. Gọi API xóa
//                if (localBook.remoteId.isNotBlank()) {
//                    apiService.deleteBook(localBook.remoteId)
//                }
//
//                // 2. API Success -> Xóa trong Realm
//                realm.write {
//                    findLatest(localBook)?.let { delete(it) }
//                }
//                Resource.Success(true)
//            } catch (e: Exception) {
//                Resource.Error(e.message ?: "DELETE ERROR")
//            }
//        }
//    }


