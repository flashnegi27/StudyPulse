package com.studypulse.app.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class StudyTrackingService : Service() {

    @Inject lateinit var notificationHelper: NotificationHelper

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var timerJob: Job? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val locationId = intent.getStringExtra(EXTRA_LOCATION_ID) ?: return START_STICKY
                val startTime  = intent.getLongExtra(EXTRA_START_TIME, System.currentTimeMillis())
                startTimer(locationId, startTime)
            }
            ACTION_STOP -> stopSelf()
        }
        return START_STICKY
    }

    private fun startTimer(locationId: String, startTime: Long) {
        startForeground(
            NotificationHelper.NOTIFICATION_ID,
            notificationHelper.buildStudyingNotification(locationId, 0L)
        )
        timerJob?.cancel()
        timerJob = serviceScope.launch {
            while (isActive) {
                val elapsed = (System.currentTimeMillis() - startTime) / 1000L
                notificationHelper.updateNotification(locationId, elapsed)
                delay(1_000L)
            }
        }
    }

    override fun onDestroy() {
        timerJob?.cancel()
        serviceScope.coroutineContext[Job]?.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        const val ACTION_START      = "com.studypulse.ACTION_START"
        const val ACTION_STOP       = "com.studypulse.ACTION_STOP"
        const val EXTRA_LOCATION_ID = "extra_location_id"
        const val EXTRA_START_TIME  = "extra_start_time"

        fun startIntent(ctx: Context, locationId: String, startTime: Long): Intent =
            Intent(ctx, StudyTrackingService::class.java).apply {
                action = ACTION_START
                putExtra(EXTRA_LOCATION_ID, locationId)
                putExtra(EXTRA_START_TIME, startTime)
            }

        fun stopIntent(ctx: Context): Intent =
            Intent(ctx, StudyTrackingService::class.java).apply {
                action = ACTION_STOP
            }
    }
}
