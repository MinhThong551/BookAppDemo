package com.example.bookappdemo.data.model

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.EmbeddedRealmObject
import io.realm.kotlin.types.RealmList

class BookDetail : EmbeddedRealmObject {

    var images: RealmList<ImageInfo> = realmListOf()

    var description: String = ""
    var summary: String = ""
    var language: String = "en"


    var rating: Double = 0.0           // rating trung bình
    var ratingCount: Int = 0           // số lượt đánh giá

    var publisher: String = ""
    var publishDate: String = ""       // ISO: yyyy-MM-dd
    var pages: Int = 0

    var categories: RealmList<String> = realmListOf()

    var price: Double = 0.0
    var currency: String = "USD"
}
