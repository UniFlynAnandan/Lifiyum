package  com.lifiyum.main.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.room.Room

import com.lifiyum.main.db.RunningDatabase
import com.lifiyum.main.other.Constants.KEY_ACTIVITY_TYPE
import com.lifiyum.main.other.Constants.KEY_FIRST_TIME_TOGGLE
import com.lifiyum.main.other.Constants.KEY_NAME
import com.lifiyum.main.other.Constants.KEY_WEIGHT
import com.lifiyum.main.other.Constants.RUNNING_DATABASE_NAME
import com.lifiyum.main.other.Constants.SHARED_PREFERENCES_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideRunningDatabase(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(
        app,
        RunningDatabase::class.java,
        RUNNING_DATABASE_NAME
    ).build()

    @Singleton
    @Provides
    fun provideRunDao(db: RunningDatabase) = db.getRunDao()


    @Provides
    @Singleton
    fun provideSharedPreference(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE)
    }
    @Singleton
    @Provides
    fun provideName(sharedPref: SharedPreferences) = sharedPref.getString(KEY_NAME, "") ?: ""


    /*@Singleton
    @Provides
    fun provideSelectActvity(sharedPref: SharedPreferences) =
        sharedPref.getString(KEY_ACTIVITY_TYPE, "") ?: ""*/

    @Singleton
    @Provides
    fun provideWeight(sharedPref: SharedPreferences) = sharedPref.getFloat(KEY_WEIGHT, 80f)

    @Singleton
    @Provides
    fun provideFirstTimeToggle(sharedPref: SharedPreferences) =
        sharedPref.getBoolean(KEY_FIRST_TIME_TOGGLE, true)



}
