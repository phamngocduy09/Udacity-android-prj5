package com.example.android.politicalpreparedness.election

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.models.ElectionDB
import kotlinx.coroutines.launch

class VoterInfoViewModel(application: Application) : ViewModel() {
    private val dbElection = ElectionDatabase.getInstance(application)
    //TODO: Add live data to hold voter info
    private val _dataVotedInfo = MutableLiveData<ElectionDB>()
    val dataVotedInfo: LiveData<ElectionDB> = _dataVotedInfo
    //TODO: Add var and methods to populate voter info
    private val _isSavedElection = MutableLiveData<Boolean>()
    val isSavedElection: LiveData<Boolean> = _isSavedElection
    //TODO: Add var and methods to support loading URLs

    //TODO: Add var and methods to save and remove elections to local database
    //TODO: cont'd -- Populate initial state of save button to reflect proper action based on election saved status
    fun findElection(id: Int) {
        viewModelScope.launch {
            try {
                val data = dbElection.electionDao.getById(id)
                if (data != null) {
                    _isSavedElection.postValue(data.isElectionSaved)
                    _dataVotedInfo.postValue(data!!)
                }
            } catch(ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    fun onClickFollow() {
        viewModelScope.launch {
            try {
                val data = _dataVotedInfo.value?.let { dbElection.electionDao.getById(it.id) }
                if (data != null) {
                    val dataUpdate = ElectionDB(data.id, data.name, data.electionDay, !data.isElectionSaved, data.division)
                    dbElection.electionDao.update(dataUpdate)
                    _isSavedElection.postValue(dataUpdate.isElectionSaved)
                    _dataVotedInfo.postValue(dataUpdate)
                }
            } catch(ex: Exception) {
                ex.printStackTrace()
            }
        }
    }
    /**
     * Hint: The saved state can be accomplished in multiple ways. It is directly related to how elections are saved/removed from the database.
     */

}