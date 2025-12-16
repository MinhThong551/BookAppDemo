package com.example.bookappdemo.data.sample

import com.example.bookappdemo.data.model.BookData

object SampleData {

    val bookList = listOf(
        BookData(
            author = "Haruki Murakami",
            name = "Kafka on the Shore",
            description = "A surreal novel blending reality and metaphysics,test test t" +
                    "adsadadasd" +
                    "" +
                    "áddas."
        ),
        BookData(
            author = "George Orwell",
            name = "1984",
            description = "A dystopian novel about totalitarianism and surveillance."
        ),
        BookData(
            author = "J.K. Rowling",
            name = "Harry Potter",
            description = "A fantasy series about a young wizard and his adventures."
        ),
        BookData(
            author = "Paulo Coelho",
            name = "The Alchemist",
            description = "A philosophical novel about following your dreams."
        )
    )

    val singleBook = BookData(
        author = "Fyodor Dostoevsky",
        name = "Crime and Punishment",
        description = "A psychological novel exploring guilt and morality."
    )
}
