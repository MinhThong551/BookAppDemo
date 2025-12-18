package com.example.bookappdemo.data.repository

import com.example.bookappdemo.data.model.Author
import com.example.bookappdemo.data.model.Book
import com.example.bookappdemo.data.model.BookDetail
import com.example.bookappdemo.data.model.ImageInfo
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.ext.realmListOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BookRepository(
    private val realm: Realm
) {

    fun observeBooks(): Flow<List<Book>> =
        realm.query<Book>()
            .asFlow()
            .map { it.list.toList() }

    fun getBookById(id: String): Book? =
        realm.query<Book>(
            "id == $0",
            org.mongodb.kbson.ObjectId(id)
        ).first().find()


    suspend fun insertSampleData() {
        realm.write {
//            val bookCount = query<Book>().count().find()
//            if (bookCount > 0L) return@write

            val author1 = copyToRealm(Author().apply {
                fullName = "Matrin"
                dob = "1990-01-01"
            })
            val author2 = copyToRealm(Author().apply {
                fullName = "jet"
                dob = "1998-02-01"
            })

            val book1 =  copyToRealm(Book().apply{
                title = "Sample Book 5 "
                author = author1
                detail = BookDetail().apply{
                    description = "Sample1 Description22222222222222sdadaasd"
                    rating = 4.5
                    images = realmListOf(ImageInfo().apply {
                        url = "https://img.lovepik.com/free-png/20210927/lovepik-book-cartoon-illustration-png-image_401539558_wh1200.png"
                        type = "cover"

                    })
                }
            })

            val book2 =  copyToRealm(Book().apply{
                title = "Sample Book 6 "
                author = author1
                detail = BookDetail().apply {
                    description = "Sample2 Description121111111111111111111" +
                            "ád" +
                            "ád" +
                            "ád" +
                            "ad" +
                            "ad" +
                            "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                            "" +
                            "" +
                            "" +
                            "" +
                            "aasadasdasdadadadadadd /n đsdfsdfdsfsd"
                    rating = 4.0
                    images = realmListOf(ImageInfo().apply {
                        url = "https://img.lovepik.com/free-png/20210927/lovepik-book-cartoon-illustration-png-image_401539558_wh1200.png"
                        type = "cover"

                    })
                }
            })
            val book3 =  copyToRealm(Book().apply{
                title = "Sample Book 7 "
                author = author2
                detail = BookDetail().apply {
                    description = "Sample3 Description 2311111111111111111111111"
                    rating = 4.5
                    images = realmListOf(ImageInfo().apply {
                        url = "https://example.com/image3.jpg"
                        type = "cover"

                    })
                }
            })
            val book4 =  copyToRealm(Book().apply{
                title = "Sample Book 8 1233333333333333333333333333333333333333"
                author = author2
                detail = BookDetail().apply {
                    description = "Sample4 Description"
                    rating = 4.5
                    images = realmListOf(ImageInfo().apply {
                        url = "https://example.com/image4.jpg"
                        type = "cover"

                    })
                }
            })

            author1.books.addAll(listOf(book1, book2))
            author2.books.addAll(listOf(book3, book4))



        }
    }
}