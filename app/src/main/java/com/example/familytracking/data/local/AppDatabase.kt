package com.example.familytracking.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.familytracking.data.local.dao.UserDao
import com.example.familytracking.data.local.entity.UserEntity

@Database(entities = [UserEntity::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}
