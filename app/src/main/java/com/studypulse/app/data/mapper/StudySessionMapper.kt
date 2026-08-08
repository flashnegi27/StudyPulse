package com.studypulse.app.data.mapper

import com.studypulse.app.data.local.entity.StudySessionEntity
import com.studypulse.app.domain.model.StudySession

fun StudySessionEntity.toDomain(): StudySession = StudySession(
    id              = id,
    locationId      = locationId,
    startTime       = startTime,
    endTime         = endTime,
    durationSeconds = durationSeconds,
    date            = date
)

fun StudySession.toEntity(): StudySessionEntity = StudySessionEntity(
    id              = id,
    locationId      = locationId,
    startTime       = startTime,
    endTime         = endTime,
    durationSeconds = durationSeconds,
    date            = date
)
