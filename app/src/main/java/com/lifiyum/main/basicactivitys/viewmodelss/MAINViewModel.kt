package com.lifiyum.main.basicactivitys.viewmodelss

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifiyum.main.db.Run
import com.lifiyum.main.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MAINViewModel

    @Inject
    constructor
    (
        val MainRepository: MainRepository, id:String
    ) : ViewModel() {

    private val getall = MainRepository.getAll()
    private val getsingledata = MainRepository.getsingledata(id)
    val runs = MediatorLiveData<List<Run>>()
    var Singledata = MediatorLiveData<Run>()
    var SelctdataFromActivity= MediatorLiveData<List<Run>>()
    init {
        runs.addSource(getall) { result ->
            result?.let {
                runs.value = it
            }
        }
    }

    fun getSingledata(id: String): MediatorLiveData<Run> {

        Singledata.addSource(MainRepository.getsingledata(id)) { result ->
            result?.let {
                Singledata.value = it
            }
        }
        return Singledata
    }

    fun getDataFromActivity(id: String):  MediatorLiveData<List<Run>> {

        SelctdataFromActivity.addSource(MainRepository.getdatafromactivity(id)) { result ->
            result?.let {
                SelctdataFromActivity.value = it
            }
        }
        return SelctdataFromActivity
    }

        fun insertRun(run: Run) = viewModelScope.launch {
            MainRepository.insertRun(run)


    }
}





