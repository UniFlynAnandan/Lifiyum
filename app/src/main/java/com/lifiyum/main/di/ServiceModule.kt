package  com.lifiyum.main.di

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat



import com.google.android.gms.location.FusedLocationProviderClient

import com.lifiyum.main.R
import com.lifiyum.main.basicactivitys.ActivityProgress_Activity
import com.lifiyum.main.other.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped
import javax.inject.Named

@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {

    @Provides
    @ServiceScoped
    fun provideFusedLocationProviderClient(@ApplicationContext appContext: Context) =
        FusedLocationProviderClient(appContext)



    @Provides
    @ServiceScoped
    fun provideMainActivityPendingIntent(@ApplicationContext appContext: Context): PendingIntent =
        PendingIntent.getActivity(
            appContext,
            0,
            Intent(appContext, ActivityProgress_Activity::class.java).also {
               /* it.action = Constants.ACTION_SHOW_TRACKING_FRAGMENT*/
            },
            PendingIntent.FLAG_UPDATE_CURRENT
        )

    @Provides
    @ServiceScoped
    @Named("baseNotificationBuilder")
    fun provideBaseNotificationBuilder(
        @ApplicationContext appContext: Context,
        pendingIntent: PendingIntent
    ): NotificationCompat.Builder =
        NotificationCompat.Builder(appContext, Constants.NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_pause_black)
            .setContentTitle(appContext.getString(R.string.running))
            .setContentText("00:00:00")
            .setContentIntent(pendingIntent)

    @Provides
    @ServiceScoped
    @Named("targetReachedNotificationBuilder")
    fun provideTargetReachedNotificationBuilder(
        @ApplicationContext appContext: Context,
        pendingIntent: PendingIntent
    ): NotificationCompat.Builder =
        NotificationCompat.Builder(appContext, Constants.NOTIFICATION_CHANNEL_TARGET_ID)
            .setAutoCancel(true)
            .setOngoing(false)
            .setOnlyAlertOnce(true)
            .setVibrate(longArrayOf(500))
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setSmallIcon(R.drawable.ic_pause_black_notiicon)
            .setContentText(appContext.getString(R.string.target_reached))
            .setContentIntent(pendingIntent)
}
/*
.setContentTitle(appContext.getString(R.string.running))*/
