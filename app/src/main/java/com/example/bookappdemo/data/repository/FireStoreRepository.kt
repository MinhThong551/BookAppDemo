package com.example.bookappdemo.data.repository

import android.util.Log
import com.example.bookappdemo.data.api.BookApiService
import com.example.bookappdemo.data.model.*
import com.example.bookappdemo.ui.base.BookDetailUiState
import com.example.bookappdemo.ui.mapper.toUiState
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
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
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FireStoreRepository @Inject constructor(
    private val realm: Realm,
    private val apiService: BookApiService,
    private val firestore: FirebaseFirestore
) {

    private var lastVisibleDocument: DocumentSnapshot? = null
    var isLastPage = false
    private val PAGE_SIZE = 10L
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
    suspend fun generate10RealBooks() {
        withContext(Dispatchers.IO) {
            val booksCollection = firestore.collection("books")

            // Danh sách 10 cuốn sách với dữ liệu thực tế
            val realBooks = listOf(
                mapOf("title" to "Clean Code", "author" to "Robert C. Martin", "price" to 35.0, "img" to "https://m.media-amazon.com/images/I/41xShlnTZTL._SX376_BO1,204,203,200_.jpg", "desc" to "A Handbook of Agile Software Craftsmanship. Bắt buộc phải đọc cho mọi lập trình viên."),
                mapOf("title" to "The Pragmatic Programmer", "author" to "David Thomas", "price" to 40.0, "img" to "https://m.media-amazon.com/images/I/51W1sBPO7tL._SX380_BO1,204,203,200_.jpg", "desc" to "Hành trình từ một Thợ code (Coder) trở thành một Nghệ nhân phần mềm (Software Craftsman)."),
                mapOf("title" to "Design Patterns", "author" to "Erich Gamma", "price" to 45.0, "img" to "https://m.media-amazon.com/images/I/51szD9HC9pL._SX395_BO1,204,203,200_.jpg", "desc" to "Elements of Reusable Object-Oriented Software (Sách GOF huyền thoại)."),
                mapOf("title" to "Atomic Habits", "author" to "James Clear", "price" to 20.0, "img" to "https://m.media-amazon.com/images/I/513Y5o-DYtL.jpg", "desc" to "Thay đổi thói quen nhỏ để tạo ra kết quả lớn. Sách self-help bán chạy nhất."),
                mapOf("title" to "Thinking, Fast and Slow", "author" to "Daniel Kahneman", "price" to 22.5, "img" to "https://m.media-amazon.com/images/I/41shZdKDjzL._SX322_BO1,204,203,200_.jpg", "desc" to "Khám phá hai hệ thống tư duy chi phối mọi quyết định của con người."),
                mapOf("title" to "Head First Design Patterns", "author" to "Eric Freeman", "price" to 38.0, "img" to "https://m.media-amazon.com/images/I/61APhXCksuL._SX430_BO1,204,203,200_.jpg", "desc" to "Học Design Pattern theo cách trực quan, dễ hiểu và cực kỳ hài hước."),
                mapOf("title" to "Dune", "author" to "Frank Herbert", "price" to 15.0, "img" to "https://m.media-amazon.com/images/I/41yX1E-nI-L._SX331_BO1,204,203,200_.jpg", "desc" to "Tiểu thuyết khoa học viễn tưởng vĩ đại nhất mọi thời đại. Hành tinh cát Arrakis."),
                mapOf("title" to "The Martian", "author" to "Andy Weir", "price" to 18.0, "img" to "https://m.media-amazon.com/images/I/41DGBBAsbHL._SX322_BO1,204,203,200_.jpg", "desc" to "Nhật ký sinh tồn cực kỳ logic và hài hước của phi hành gia kẹt trên sao Hỏa."),
                mapOf("title" to "Sapiens", "author" to "Yuval Noah Harari", "price" to 25.0, "img" to "https://m.media-amazon.com/images/I/41yu2qXhXXL._SX324_BO1,204,203,200_.jpg", "desc" to "Lược sử loài người. Chúng ta từ đâu đến và sẽ đi về đâu?"),
                mapOf("title" to "Kotlin in Action", "author" to "Dmitry Jemerov", "price" to 32.0, "img" to "https://m.media-amazon.com/images/I/41E+OExwYpL._SX396_BO1,204,203,200_.jpg", "desc" to "Sách gối đầu giường để master ngôn ngữ Kotlin từ cơ bản đến nâng cao.")
            )

            realBooks.forEachIndexed { index, book ->
                val bookData = hashMapOf(
                    "title" to book["title"],
                    "authorName" to book["author"],
                    "description" to book["desc"],
                    "summary" to "Tóm tắt nhanh: ${book["title"]}",
                    "price" to book["price"],
                    "currency" to "USD",
                    "rating" to 4.5 + (index % 5) * 0.1,
                    "ratingCount" to (100 + index * 57),
                    "pages" to (250 + index * 30),
                    "language" to "en",
                    "publisher" to "NXB Kỹ Thuật Số",
                    "publishDate" to "2023-10-01",
                    "images" to listOf(book["img"])
                )

                booksCollection.add(bookData)
            }
        }
    }


    suspend fun loadNextPage() {
        if (isLastPage) return

        withContext(Dispatchers.IO) {
            var query = firestore.collection("books")
                .orderBy(FieldPath.documentId())
                .limit(PAGE_SIZE)

            lastVisibleDocument?.let {
                query = query.startAfter(it)
            }

            query.get().addOnSuccessListener { snapshot ->
                if (snapshot.isEmpty) {
                    isLastPage = true
                    return@addOnSuccessListener
                }

                lastVisibleDocument = snapshot.documents.lastOrNull()

                CoroutineScope(Dispatchers.IO).launch {
                    realm.write {
                        for (document in snapshot.documents) {
                            try {
                                val uiState = document.toUiState()
                                saveToRealmInternal(this, uiState)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }.addOnFailureListener { e ->
                e.printStackTrace()
            }
        }
    }

    suspend fun refreshData() {
        lastVisibleDocument = null
        isLastPage = false
        loadNextPage()
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