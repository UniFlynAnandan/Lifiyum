package  com.lifiyum.main.repositories


import com.lifiyum.main.db.Run
import com.lifiyum.main.db.RunDao
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val runDao: RunDao
) {
    suspend fun insertRun(run: Run)=runDao.insertRun(run)
    suspend fun deleteRun(run:Run)=runDao.deleteRun(run)
    fun getAllRunsSortedByDate() = runDao.getAllRunsSortedByDate()

  //  fun getAllRunsSortedByDistance(type:String) = runDao.getAllRunsSortedByDistance(type)
  fun getAllRunsSortedByDistance() = runDao.getAllRunsSortedByDistance()

    fun getAllRunsSortedByTimeInMillis() = runDao.getAllRunsSortedByTimeInMillis()

    fun getAllRunsSortedByAvgSpeed() = runDao.getAllRunsSortedByAvgSpeed()

    fun getAllRunsSortedByCaloriesBurned() = runDao.getAllRunsSortedByCaloriesBurned()

    fun getAll()=runDao.getAll()
    fun getsingledata(id:String)=runDao.getsingledata(id)

    fun getdatafromactivity(activity_Type:String)=runDao.getdatafromactivity(activity_Type)

    fun getTotalAvgSpeed() = runDao.getTotalAvgSpeed()

    fun getTotalDistance() = runDao.getTotalDistance()

    fun getTotalCaloriesBurned() = runDao.getTotalCaloriesBurned()

    fun getTotalTimeInMillis() = runDao.getTotalTimeInMillis()

}