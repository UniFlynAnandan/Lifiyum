package com.lifiyum.main.other

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.fragment.app.Fragment
import com.lifiyum.main.R
import pub.devrel.easypermissions.EasyPermissions
import java.text.DecimalFormat
import java.util.concurrent.TimeUnit

object TrackingUtility {

    fun hasLocationPermissions(context: Context) =
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.hasPermissions(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        } else {
            EasyPermissions.hasPermissions(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }

    fun requestPermissions(fragment: Fragment) {
        if (hasLocationPermissions(fragment.requireContext())) return

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.requestPermissions(
                fragment,
                fragment.requireContext().getString(R.string.location_permissions_required_info),
                Constants.REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else {
            EasyPermissions.requestPermissions(
                fragment,
                fragment.requireContext().getString(R.string.location_permissions_required_info),
                Constants.REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }
    }

    fun getFormattedStopWatchTime(millis: Long, includeMillis: Boolean = false): String {
        var milliseconds = millis
        val hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
        milliseconds -= TimeUnit.HOURS.toMillis(hours)

        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
        milliseconds -= TimeUnit.MINUTES.toMillis(minutes)

        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds)

        if (!includeMillis) {
            return "${if (hours < 10) "0" else ""}$hours:" +
                    "${if (minutes < 10) "0" else ""}$minutes:" +
                    "${if (seconds < 10) "0" else ""}$seconds"
        }

        milliseconds -= TimeUnit.SECONDS.toMillis(seconds)
        milliseconds /= 10
        return "${if (hours < 10) "0" else ""}$hours:" +
                "${if (minutes < 10) "0" else ""}$minutes:" +
                "${if (seconds < 10) "0" else ""}$seconds" /*+
                "${if (milliseconds < 10) "0" else ""}$milliseconds"*/
    }



    fun getFormattedDistance(km: Long ): String {
        val newDistancetokm = km / 1000.0
        val df = DecimalFormat("#.##")
        /*df.roundingMode = RoundingMode.CEILING*/
        val formatDistance = df.format(newDistancetokm)
        return formatDistance
    }
    fun getFormattedElevation(km: Double ): Double {

        val df = DecimalFormat("#.##")
       /* df.roundingMode = RoundingMode.CEILING*/
        val format_Elevation = df.format(km)
        val d = format_Elevation.toDouble()
        return d
    }

    fun getFormattedElevationinsert(km: Double ): Double {

        val df = DecimalFormat("#.#")
        /* df.roundingMode = RoundingMode.CEILING*/
        val format_Elevation = df.format(km)
        val d = format_Elevation.toDouble()
        return d
    }

    fun getFormatedMinitus(millis: Long) :Long
    {
        val minutes: Long = millis / 1000 / 60

        return minutes

    }
}
