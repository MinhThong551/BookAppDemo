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
import javax.inject.Singleton

@Singleton
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


