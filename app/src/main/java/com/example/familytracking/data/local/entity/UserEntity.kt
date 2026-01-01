package com.example.familytracking.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.familytracking.domain.model.User

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String
) {
    fun toDomain(): User {
        return User(
            id = id,
            name = name,
            email = email
        )
    }
}
