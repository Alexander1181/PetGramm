package com.example.foroapp.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.foroapp.data.local.Converters
import com.example.foroapp.data.local.comment.CommentDao
import com.example.foroapp.data.local.comment.CommentEntity
import com.example.foroapp.data.local.user.UserEntity
import com.example.foroapp.data.local.post.PostEntity
import com.example.foroapp.data.local.user.UserDao
import com.example.foroapp.data.local.post.PostDao

import com.example.foroapp.data.local.notification.NotificationDao
import com.example.foroapp.data.local.notification.NotificationEntity

@Database(
    entities = [UserEntity::class, PostEntity::class, CommentEntity::class, NotificationEntity::class],
    version = 9, // Incremented version for user roles
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase(){
    abstract fun userDao(): UserDao
    abstract fun postDao(): PostDao
    abstract fun commentDao(): CommentDao
    abstract fun notificationDao(): NotificationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        private const val DB_NAME = "foroapp.db"

        fun getInstance(context: Context): AppDatabase{
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DB_NAME
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}