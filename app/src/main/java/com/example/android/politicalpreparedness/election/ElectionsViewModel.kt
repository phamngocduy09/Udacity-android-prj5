package com.example.android.politicalpreparedness.election

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.ElectionDB
import kotlinx.coroutines.launch

//TODO: Construct ViewModel and provide election datasource
class ElectionsViewModel(application: Application) : ViewModel() {
    private val electionDB = ElectionDatabase.getInstance(application)

    //TODO: Create live data val for upcoming elections
    val upcomingData: LiveData<List<ElectionDB>> = electionDB.electionDao.getAllUpcoming()
    val savedData: LiveData<List<ElectionDB>> = electionDB.electionDao.getAllSaved()

    //TODO: Create live data val for saved elections
    //TODO: Create val and functions to populate live data for upcoming elections from the API and saved elections from local database

    init {
        getData()
    }

    private fun getData() {
        viewModelScope.launch {
            val data = CivicsApi.retrofitService.getElections()
            val dataInsertDB = data.elections.map {
                ElectionDB(it.id, it.name, it.electionDay, false, it.division)
            }
            electionDB.electionDao.insertAll(dataInsertDB)
        }
    }
    //TODO: Create functions to navigate to saved or upcoming election voter info

}