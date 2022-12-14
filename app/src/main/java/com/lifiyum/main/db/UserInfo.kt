package  com.lifiyum.main.db

import android.content.SharedPreferences
import com.lifiyum.main.other.Constants.KEY_ACTIVITY_TYPE
import com.lifiyum.main.other.Constants.KEY_FIRST_TIME_TOGGLE
import com.lifiyum.main.other.Constants.KEY_NAME
import com.lifiyum.main.other.Constants.KEY_TARGET_TYPE
import com.lifiyum.main.other.Constants.KEY_TARGET_VALUE
import com.lifiyum.main.other.Constants.KEY_WEIGHT
import com.lifiyum.main.other.TargetType

import javax.inject.Inject

data class UserInfo @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {

    private var _name: String = loadNameFromSharedPreferences()
    val name get() = _name

    private var _weight: Float = loadWeightFromSharedPreferences()
    val weight get() = _weight

    private var _isFirstToggle: Boolean = loadToggleInfoFromSharedPreferences()
    val isFirstToggle get() = _isFirstToggle

    private var _targetType: TargetType = loadTargetTypeFromSharedPreferences()
    val targetType get() = _targetType

    private var _activityType:String=loadTargetActvityTypeFromSharedPreferences()
    val activityType get() = _activityType

    fun applyChanges(
        name: String = this.name,
        weight: Float = this.weight,
        targetType: TargetType = TargetType.NONE,
        isFirstToggle: Boolean = this.isFirstToggle ,
        activityType:String =this.activityType
    ) {
        _name = name
        _weight = weight
        _isFirstToggle = isFirstToggle
        _targetType = targetType
        _activityType=activityType
        sharedPreferences.edit()
            .putString(KEY_NAME, name)
            .putFloat(KEY_WEIGHT, weight)
            .putString(KEY_TARGET_TYPE, targetType.name)
            .putFloat(KEY_TARGET_VALUE, targetType.value)
            .putBoolean(KEY_FIRST_TIME_TOGGLE, isFirstToggle)
            .putString(KEY_ACTIVITY_TYPE,activityType)
            .apply()
    }

    private fun loadNameFromSharedPreferences() =
        sharedPreferences.getString(KEY_NAME, "User") ?: "User".also { _name = it }

    private fun loadWeightFromSharedPreferences() =
        sharedPreferences.getFloat(KEY_WEIGHT, 80f).also { _weight = it }

    private fun loadToggleInfoFromSharedPreferences() =
        sharedPreferences.getBoolean(KEY_FIRST_TIME_TOGGLE, true).also { _isFirstToggle = it }

    private fun loadTargetTypeFromSharedPreferences() =
        when (sharedPreferences.getString(KEY_TARGET_TYPE, null)) {
            TargetType.TIME.name -> TargetType.TIME
            TargetType.DISTANCE.name -> TargetType.DISTANCE
            TargetType.CALORIES.name -> TargetType.CALORIES
            else -> TargetType.NONE
        }.also { loadTargetTypeValueFromSharedPreferences(it) }.also { loadTargetActvityTypeFromSharedPreferences() }

    private fun loadTargetActvityTypeFromSharedPreferences() =
        sharedPreferences.getString("KEY_ACTIVITY_TYPE", "run") ?: "run".also { _activityType = it }


    private fun loadTargetTypeValueFromSharedPreferences(targetType: TargetType) {
        targetType.value = sharedPreferences.getFloat(KEY_TARGET_VALUE, 0f)
    }
}
