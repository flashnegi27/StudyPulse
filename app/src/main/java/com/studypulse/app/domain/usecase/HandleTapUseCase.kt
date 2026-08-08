package com.studypulse.app.domain.usecase

import com.studypulse.app.data.datastore.SessionStateDataStore
import com.studypulse.app.domain.model.SessionState
import com.studypulse.app.domain.model.StudySession
import com.studypulse.app.domain.repository.StudyRepository
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

sealed class TapResult {
    data class SessionStarted(val locationId: String, val startTime: Long) : TapResult()
    data class SessionStopped(val durationSeconds: Long, val locationId: String) : TapResult()
    data class Error(val message: String) : TapResult()
}

class HandleTapUseCase @Inject constructor(
    private val dataStore:  SessionStateDataStore,
    private val repository: StudyRepository
) {
    suspend operator fun invoke(locationId: String): TapResult {
        return when (val state = dataStore.getSessionState().first()) {
            is SessionState.Idle -> {
                val startTime = System.currentTimeMillis()
                dataStore.saveStudyingState(locationId, startTime)
                TapResult.SessionStarted(locationId, startTime)
            }
            is SessionState.Studying -> {
                val endTime      = System.currentTimeMillis()
                val durationSecs = (endTime - state.startTime) / 1000L
                val session = StudySession(
                    id              = 0,
                    locationId      = state.locationId,
                    startTime       = state.startTime,
                    endTime         = endTime,
                    durationSeconds = durationSecs,
                    date            = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
                )
                repository.insertSession(session)
                dataStore.clearState()
                TapResult.SessionStopped(durationSecs, state.locationId)
            }
        }
    }
}
