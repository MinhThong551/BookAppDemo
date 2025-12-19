package com.example.bookappdemo.data.repository

import com.example.bookappdemo.MyApp
import com.example.bookappdemo.MyApp.Companion.realm
import com.example.bookappdemo.data.model.Author
import com.example.bookappdemo.data.model.Book
import com.example.bookappdemo.data.model.BookDetail
import com.example.bookappdemo.data.model.ImageInfo
import com.example.bookappdemo.ui.base.BookDetailUiState
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.ext.realmListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class BookRepository(
    private val realm: Realm
) {

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

    suspend fun insertSampleData() {
        withContext(Dispatchers.IO) {
            realm.write {
//            val bookCount = query<Book>().count().find()
//            if (bookCount > 0L) return@write

                val author1 = copyToRealm(Author().apply {
                    fullName = "George Orwell"
                    dob = "1903-06-25"
                })

                val author2 = copyToRealm(Author().apply {
                    fullName = "Harper Lee"
                    dob = "1926-04-28"
                })

                val book1 = copyToRealm(Book().apply {
                    title = "1984"
                    author = author1
                    detail = BookDetail().apply {

                        description =
                            "A dystopian social science fiction novel and cautionary tale about the dangers of totalitarianism."

                        summary =
                            "The story follows Winston Smith, a low-ranking member of 'the Party', who is frustrated by the omnipresent eyes of the Party."

                        language = "en"
                        rating = 4.7
                        ratingCount = 1250000

                        publisher = "Secker & Warburg"
                        publishDate = "1949-06-08"
                        pages = 328

                        categories = realmListOf(
                            "Dystopian",
                            "Political Fiction",
                            "Science Fiction"
                        )

                        price = 9.99
                        currency = "USD"

                        images = realmListOf(
                            ImageInfo().apply {
                                url = "https://covers.openlibrary.org/b/id/7222246-L.jpg"
                                type = "cover"
                            },
                            ImageInfo().apply {
                                url =
                                    "https://www.harpercollins.com/cdn/shop/files/9780547249643.jpg?v=1757675146"
                                type = "gallery"
                            },
                            ImageInfo().apply {
                                url =
                                    "https://pro2-bar-s3-cdn-cf6.myportfolio.com/d898e1369ef62545efef48866827b472/0e43328defcd01ddd666b919_rw_1920.jpg?h=dc5ea6d25a86b2eb2592968bea81d047"
                                type = "gallery"
                            }
                        )
                    }
                })


                val book2 = copyToRealm(Book().apply {
                    title = "Animal Farm"
                    author = author1
                    detail = BookDetail().apply {

                        description =
                            "A satirical allegorical novella reflecting events leading up to the Russian Revolution."

                        summary =
                            "Farm animals overthrow their human farmer, hoping to create a society where animals can be equal."

                        language = "en"
                        rating = 4.5
                        ratingCount = 980000

                        publisher = "Secker & Warburg"
                        publishDate = "1945-08-17"
                        pages = 112

                        categories = realmListOf(
                            "Satire",
                            "Political Allegory"
                        )

                        price = 7.99
                        currency = "USD"

                        images = realmListOf(
                            ImageInfo().apply {
                                url =
                                    "https://cdn2.penguin.com.au/covers/original/9780141036137.jpg"
                                type = "cover"
                            },
                            ImageInfo().apply {
                                url =
                                    "https://cdn2.penguin.com.au/covers/original/9780140817690.jpg"
                                type = "gallery"
                            },
                            ImageInfo().apply {
                                url =
                                    "https://cdn2.penguin.com.au/covers/original/9781473581586.jpg"
                                type = "gallery"
                            }
                        )
                    }
                })

                val book3 = copyToRealm(Book().apply {
                    title = "To Kill a Mockingbird"
                    author = author2
                    detail = BookDetail().apply {

                        description =
                            "A novel about the serious issues of rape and racial inequality."

                        summary =
                            "The story is told through the eyes of Scout Finch, a young girl growing up in the American South."

                        language = "en"
                        rating = 4.8
                        ratingCount = 1500000

                        publisher = "J.B. Lippincott & Co."
                        publishDate = "1960-07-11"
                        pages = 281

                        categories = realmListOf(
                            "Classic",
                            "Historical Fiction"
                        )

                        price = 10.99
                        currency = "USD"

                        images = realmListOf(
                            ImageInfo().apply {
                                url = "https://covers.openlibrary.org/b/id/8228691-L.jpg"
                                type = "cover"
                            },
                            ImageInfo().apply {
                                url = "https://covers.openlibrary.org/b/id/10958394-L.jpg"
                                type = "illustration"
                            }
                        )
                    }
                })

                author1.books.addAll(listOf(book1, book2))
                author2.books.add(book3)
            }
        }
    }
    suspend fun addBook(
        title: String,
        author: Author,
        detail: BookDetail
    ) {
        realm.write {
            copyToRealm(
                Book().apply {
                    this.title = title
                    this.author = findLatest(author)
                    this.detail = detail
                }
            )
        }
    }
        suspend fun updateBook(
            bookId: String,
            update: BookDetail.() -> Unit
        ) {
           realm.write {
                val book = query<Book>(
                    "id == $0",
                    org.mongodb.kbson.ObjectId(bookId)
                ).first().find()

                book?.detail?.apply(update)
            }
        }


    suspend fun deleteBook(bookId: String) {
        realm.write {
            val book = query<Book>(
                "id == $0",
                org.mongodb.kbson.ObjectId(bookId)
            ).first().find()

            book?.let { delete(it) }
        }
    }

    suspend fun addSimpleBook(title: String, authorName: String) {
        realm.write {
            val newAuthor = Author().apply { fullName = authorName }
            val newBook = Book().apply {
                this.title = title
                this.author = copyToRealm(newAuthor)
                this.detail = BookDetail().apply {
                    description = "Chưa có mô tả"
                    language = "vi"
                }
            }
            copyToRealm(newBook)
        }
    }
    suspend fun updateBookFull(
        bookId: String,
        uiState: BookDetailUiState
    ) {
        withContext(Dispatchers.IO) {
            realm.write {
                val book = query<Book>(
                    "id == $0",
                    org.mongodb.kbson.ObjectId(bookId)
                ).first().find() ?: return@write
                book.title = uiState.title

                if (book.author != null) {
                    book.author?.fullName = uiState.authorName
                } else {
                    val newAuthor = Author().apply { fullName = uiState.authorName }
                    book.author = copyToRealm(newAuthor)
                }
                if (book.detail == null) {
                    book.detail = BookDetail()
                }

                book.detail?.apply {
                    description = uiState.description
                    summary = uiState.summary
                    language = uiState.language
                    publisher = uiState.publisher
                    publishDate = uiState.publishDate
                    pages = uiState.pages
                    rating = uiState.rating
                    ratingCount = uiState.ratingCount
                    price = uiState.price
                    currency = uiState.currency


                    images.clear()
                    uiState.images.forEach { urlString ->
                        val imageObj = ImageInfo().apply {
                            url = urlString
                            type = "gallery"
                        }
                        images.add(imageObj)
                    }
                }
            }
        }
    }
}



