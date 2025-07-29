package com.example.deflatam_chatapp.di


import android.content.Context
import androidx.room.Room
import com.example.deflatam_chatapp.data.database.AppDatabase
import com.example.deflatam_chatapp.data.database.dao.ChatRoomDao
import com.example.deflatam_chatapp.data.database.dao.MessageDao
import com.example.deflatam_chatapp.data.database.dao.UserDao
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo Hilt para proveer dependencias relacionadas con la base de datos (Firebase y Room).
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * Provee una instancia de FirebaseAuth.
     * @return Instancia de [FirebaseAuth].
     */
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    /**
     * Provee una instancia de FirebaseFirestore.
     * @return Instancia de [FirebaseFirestore].
     */
    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    /**
     * Provee una instancia de FirebaseStorage.
     * @return Instancia de [FirebaseStorage].
     */
    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage {
        return FirebaseStorage.getInstance()
    }

    /**
     * Provee una instancia de FirebaseMessaging.
     * @return Instancia de [FirebaseMessaging].
     */
    @Provides
    @Singleton
    fun provideFirebaseMessaging(): FirebaseMessaging {
        return FirebaseMessaging.getInstance()
    }

    /**
     * Provee la instancia de la base de datos Room.
     * @param context Contexto de la aplicación.
     * @return Instancia de [AppDatabase].
     */
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "chat_app_database" // Nombre de la base de datos.
        )
            .fallbackToDestructiveMigration() // Estrategia de migración destructiva (solo para desarrollo).
            .build()
    }

    /**
     * Provee una instancia de UserDao.
     * @param appDatabase La base de datos de la aplicación.
     * @return Instancia de [UserDao].
     */
    @Provides
    @Singleton
    fun provideUserDao(appDatabase: AppDatabase): UserDao {
        return appDatabase.userDao()
    }

    /**
     * Provee una instancia de ChatRoomDao.
     * @param appDatabase La base de datos de la aplicación.
     * @return Instancia de [ChatRoomDao].
     */
    @Provides
    @Singleton
    fun provideChatRoomDao(appDatabase: AppDatabase): ChatRoomDao {
        return appDatabase.chatRoomDao()
    }

    /**
     * Provee una instancia de MessageDao.
     * @param appDatabase La base de datos de la aplicación.
     * @return Instancia de [MessageDao].
     */
    @Provides
    @Singleton
    fun provideMessageDao(appDatabase: AppDatabase): MessageDao {
        return appDatabase.messageDao()
    }
}
