package com.example.bookappdemo.data.repository

import BookApiService
import com.example.bookappdemo.data.api.RetrofitClient
import com.example.bookappdemo.data.model.*
import com.example.bookappdemo.ui.base.BookDetailUiState
import com.example.bookappdemo.ui.mapper.toUiState
import com.google.firebase.Firebase
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FirestoreRepository(
    private val realm: Realm,
    private val apiService: BookApiService = RetrofitClient.instance
) {


    private var firestoreListener: ListenerRegistration? = null

    fun observeBooks(): Flow<List<Book>> {
        return realm.query<Book>()
            .asFlow()
            .map { it.list.toList() }
            .flowOn(Dispatchers.IO)
    }

    fun observeBookById(id: String): Flow<Book?> {
        return try {
            realm.query<Book>("id == $0", org.mongodb.kbson.ObjectId(id))
                .asFlow()
                .map { it.list.firstOrNull() }
                .flowOn(Dispatchers.IO)
        } catch (e: Exception) {
            kotlinx.coroutines.flow.flowOf(null)
        }
    }

    fun getLocalBookById(id: String): Book? {
        return try {
            realm.query<Book>("id == $0", org.mongodb.kbson.ObjectId(id))
                .first()
                .find()
        } catch (e: Exception) { null }
    }


    suspend fun fetchAndSyncData(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getRecommendedBooks()

                if (response.isNotEmpty()) {
                    startRealtimeSync()
                    return@withContext true
                }
                return@withContext false
            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext false
            }
        }
    }

    private fun startRealtimeSync() {
        firestoreListener?.remove()

        firestoreListener = Firebase.firestore.collection("books")
            .addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener

                if (snapshot != null) {
                    CoroutineScope(Dispatchers.IO).launch {
                        realm.write {
                            for (dc in snapshot.documentChanges) {
                                val uiState = dc.document.toUiState()
                                when (dc.type) {
                                    com.google.firebase.firestore.DocumentChange.Type.ADDED,
                                    com.google.firebase.firestore.DocumentChange.Type.MODIFIED -> {
                                        saveToRealmInternal(this, uiState) // Gọi hàm nội bộ
                                    }
                                    com.google.firebase.firestore.DocumentChange.Type.REMOVED -> {
                                        val book = query<Book>("remoteId == $0", dc.document.id).first().find()
                                        if (book != null) delete(book)
                                    }
                                }
                            }
                        }
                    }
                }
            }
    }

    fun stopRealtimeSync() {
        firestoreListener?.remove()
    }
    private fun saveToRealmInternal(transaction: io.realm.kotlin.MutableRealm, item: BookDetailUiState) {
        with(transaction) {
            val authorName = item.authorName
            var authorObj = query<Author>("fullName == $0", authorName).first().find()
            if (authorObj == null) authorObj = copyToRealm(Author().apply { fullName = authorName })

            val detailObj = BookDetail().apply {
                this.description = item.description
                this.summary = item.summary
                this.price = item.price
                this.rating = item.rating

                this.ratingCount = item.ratingCount
                this.pages = item.pages
                this.language = item.language
                this.publisher = item.publisher
                this.publishDate = item.publishDate
                this.currency = item.currency
                item.images.forEach { url -> this.images.add(ImageInfo().apply { this.url = url }) }
            }

            val existingBook = query<Book>("remoteId == $0", item.id).first().find()
            if (existingBook == null) {
                val newBook = Book().apply {
                    this.id = org.mongodb.kbson.ObjectId()
                    this.remoteId = item.id
                    this.title = item.title
                    this.author = authorObj
                    this.detail = detailObj
                }
                val savedBook = copyToRealm(newBook)
                authorObj.books.add(savedBook)
            } else {
                existingBook.title = item.title
                existingBook.detail = detailObj
                existingBook.author = authorObj
            }
        }
    }


}