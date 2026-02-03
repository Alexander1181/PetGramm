package com.example.foroapp.data.repository

import com.example.foroapp.data.local.notification.NotificationDao
import com.example.foroapp.data.local.notification.NotificationEntity
import kotlinx.coroutines.flow.Flow

class NotificationRepository(private val notificationDao: NotificationDao) {

    fun getNotificationsForUser(userId: String): Flow<List<NotificationEntity>> {
        return notificationDao.getNotificationsForUser(userId)
    }

    suspend fun insert(notification: NotificationEntity) {
        notificationDao.insertNotification(notification)
    }

    suspend fun delete(id: Long) {
        notificationDao.deleteNotification(id)
    }
}
