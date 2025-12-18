package com.example.bookappdemo.data.model

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class Author: RealmObject {
    @PrimaryKey
    var id : ObjectId = ObjectId()
    var fullName:String = ""
    var dob :String =""
    var books : RealmList<Book> = realmListOf()




}
