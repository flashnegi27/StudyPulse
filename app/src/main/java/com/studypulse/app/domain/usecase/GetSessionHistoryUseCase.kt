package com.studypulse.app.domain.usecase

import com.studypulse.app.domain.model.StudySession
import com.studypulse.app.domain.repository.StudyRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSessionHistoryUseCase @Inject constructor(
    private val repository: StudyRepository
) {
    operator fun invoke(): Flow<List<StudySession>> = repository.getAllSessions()
}
