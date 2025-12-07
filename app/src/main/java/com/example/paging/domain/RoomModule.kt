package com.example.paging.domain

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.paging.domain.dao.RepoAndRemoteKeyDao
import com.example.paging.domain.dao.RepoDao
import com.example.paging.domain.dao.RemoteKeyDao
import com.example.paging.domain.entities.Repo
import com.example.paging.domain.entities.RepoAndRemoteKey
import com.example.paging.domain.entities.RemoteKey
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import java.util.concurrent.Executors

@Database(
    entities = [Repo::class, RemoteKey::class, RepoAndRemoteKey::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun repoDao(): RepoDao
    abstract fun repoRemoteKeyDao(): RemoteKeyDao
    abstract fun repoAndRemoteKeyDao(): RepoAndRemoteKeyDao
}

@Module
class RoomModule {
    @Single
    fun provideRoomDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "demo_db")
            // https://arjun30.medium.com/printing-room-db-queries-in-logcat-82730e5133db
            .setQueryCallback({ sqlQuery, bindArgs ->
                Log.d("DBModule", "SQL Query: $sqlQuery SQL Args: $bindArgs")
            }, Executors.newSingleThreadExecutor())
            .fallbackToDestructiveMigration(true)
            .build()
    }

    @Single
    fun provideRepoDao(appDatabase: AppDatabase): RepoDao = appDatabase.repoDao()

    @Single
    fun provideRemoteKeyDao(appDatabase: AppDatabase): RemoteKeyDao =
        appDatabase.repoRemoteKeyDao()

    @Single
    fun provideRepoAndRemoteKeyDao(appDatabase: AppDatabase): RepoAndRemoteKeyDao =
        appDatabase.repoAndRemoteKeyDao()
}