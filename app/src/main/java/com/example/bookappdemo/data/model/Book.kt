package com.example.bookappdemo.data.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class Book: RealmObject {
    @PrimaryKey
    var id : ObjectId = ObjectId()
    var remoteId: String = ""
    var title:String = ""
    var author: Author?=null
    var detail: BookDetail?=null
}


