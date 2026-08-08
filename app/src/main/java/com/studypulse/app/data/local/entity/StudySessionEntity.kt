package com.studypulse.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "study_sessions",
    indices   = [Index(value = ["date"])]
)
data class StudySessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val locationId: String,
    val startTime: Long,
    val endTime: Long,
    val durationSeconds: Long,
    val date: String
)
