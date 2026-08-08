package com.studypulse.app.domain.usecase

import com.studypulse.app.domain.repository.StudyRepository
import javax.inject.Inject

class DeleteSessionUseCase @Inject constructor(
    private val repository: StudyRepository
) {
    suspend operator fun invoke(id: Long) {
        repository.deleteSession(id)
    }
}
