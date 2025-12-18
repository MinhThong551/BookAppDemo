package com.example.bookappdemo.data.model

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.EmbeddedRealmObject
import io.realm.kotlin.types.RealmList

class BookDetail : EmbeddedRealmObject {
    var images : RealmList<ImageInfo> = realmListOf()
    var rating :Double =0.0
    var language: String = "en"
    var description :String =""
    var publisher: String = ""
    var publishDate: String = ""
    var categories: RealmList<String> = realmListOf()
}