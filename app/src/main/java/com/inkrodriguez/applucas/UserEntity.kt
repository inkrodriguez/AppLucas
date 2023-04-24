package com.inkrodriguez.applucas

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class UserEntity(
    var email: String = "",
    var password: String = "",
    var latitude: String = "",
    var longitude: String = "") {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}

