package com.example.android.politicalpreparedness.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.android.politicalpreparedness.network.models.ElectionDB

@Dao
interface ElectionDao {

    //TODO: Add insert query
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(elections: List<ElectionDB>)

    //TODO: Add select all election query
    @Query("SELECT * FROM election_table")
    fun getAllUpcoming(): LiveData<List<ElectionDB>>

    @Query("SELECT * FROM election_table where isElectionSaved = 1")
    fun getAllSaved(): LiveData<List<ElectionDB>>

    //TODO: Add select single election query
    @Query("SELECT * FROM election_table WHERE id = :id")
    suspend fun getById(id: Int) : ElectionDB?

    @Update
    suspend fun update(election: ElectionDB)

    //TODO: Add delete query
    @Delete
    suspend fun delete(election: ElectionDB)

    //TODO: Add clear query
    @Query("DELETE FROM election_table")
    suspend fun clear()
}