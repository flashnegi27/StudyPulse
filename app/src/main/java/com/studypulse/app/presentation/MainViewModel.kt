package com.studypulse.app.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studypulse.app.domain.usecase.HandleTapUseCase
import com.studypulse.app.domain.usecase.TapResult
import com.studypulse.app.service.StudyTrackingService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val handleTapUseCase: HandleTapUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    fun handleTap(locationId: String) {
        viewModelScope.launch {
            when (val result = handleTapUseCase(locationId)) {
                is TapResult.SessionStarted -> {
                    context.startForegroundService(
                        StudyTrackingService.startIntent(context, result.locationId, result.startTime)
                    )
                }
                is TapResult.SessionStopped -> {
                    context.stopService(StudyTrackingService.stopIntent(context))
                }
                is TapResult.Error -> {
                    // Errors surface through the DataStore flow to the UI
                }
            }
        }
    }
}
