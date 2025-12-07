package com.example.paging.domain.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.paging.domain.entities.RemoteKey

@Dao
interface RemoteKeyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg remoteKey: RemoteKey): List<Long>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(remoteKey: RemoteKey): Int

    @Delete
    suspend fun delete(remoteKey: RemoteKey): Int

    @Query("DELETE FROM remote_keys WHERE label = :label")
    suspend fun deleteByLabel(label: String): Int

    @Query("DELETE FROM remote_keys WHERE created_at < ((strftime('%s', 'now') - :hours*3600))*1000")
    suspend fun removeOutdatedKeys(hours: Long): Int

    @Query("SELECT * FROM remote_keys WHERE label = :label")
    suspend fun queryByLabel(label: String): RemoteKey?

    @Query("DELETE FROM remote_keys")
    suspend fun deleteAll(): Int
}