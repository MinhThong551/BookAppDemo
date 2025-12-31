package com.example.bookappdemo.di.module

import com.example.bookappdemo.data.api.BookApiService
import com.example.bookappdemo.data.model.Author
import com.example.bookappdemo.data.model.Book
import com.example.bookappdemo.data.model.BookDetail
import com.example.bookappdemo.data.model.ImageInfo
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import dagger.Module
import dagger.Provides
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
class AppModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://694a13921282f890d2d78b18.mockapi.io/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideBookApiService(retrofit: Retrofit): BookApiService{
        return retrofit.create(BookApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideRealm() :Realm {
        val config = RealmConfiguration.create(
            schema = setOf(
                Author::class,
                Book::class,
                BookDetail::class,
                ImageInfo::class
            ))
        return Realm.open(config)
    }

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore {
        return Firebase.firestore
    }

}