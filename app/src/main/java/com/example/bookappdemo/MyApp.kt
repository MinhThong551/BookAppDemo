package com.example.bookappdemo

import android.app.Application
import android.util.Log
import com.example.bookappdemo.data.model.Author
import com.example.bookappdemo.data.model.Book
import com.example.bookappdemo.data.model.BookDetail
import com.example.bookappdemo.data.model.ImageInfo
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration

class MyApp : Application() {

    companion object {
        lateinit var realm: Realm
    }

    override fun onCreate() {
        super.onCreate()

        val config = RealmConfiguration.create(
            schema = setOf(
                Author::class,
                Book::class,
                BookDetail::class,
                ImageInfo::class
            )
        )

        realm = Realm.open(config)

        Log.d("REALM_PATH", "Realm path = ${config.path}")
    }
}
