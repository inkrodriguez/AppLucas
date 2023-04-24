package com.inkrodriguez.applucas


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [UserEntity::class], version = 1)
abstract class AppDataBase: RoomDatabase() {

    abstract fun getUserDao(): Dao

    companion object {

        @Volatile
        private var instance: AppDataBase? = null
        private val LOCK = Any()


        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            AppDataBase::class.java,
            "database-users")
            .fallbackToDestructiveMigration()
            .build()
    }

}