package com.example.android.politicalpreparedness.network.models

import androidx.room.*
import com.squareup.moshi.*
import java.util.*

data class Election(
        val id: Int,
        val name: String,
        val electionDay: Date,
        @Embedded(prefix = "division_") @Json(name="ocdDivisionId")val division: Division
)

@Entity(tableName = "election_table")
data class ElectionDB(
        @PrimaryKey val id: Int,
        @ColumnInfo(name = "name")val name: String,
        @ColumnInfo(name = "electionDay")val electionDay: Date,
        @ColumnInfo(name = "isElectionSaved")val isElectionSaved: Boolean,
        @Embedded(prefix = "division_") @Json(name="ocdDivisionId") val division: Division
)