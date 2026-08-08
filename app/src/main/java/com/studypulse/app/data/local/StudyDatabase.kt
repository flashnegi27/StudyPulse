package com.studypulse.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.studypulse.app.data.local.dao.StudySessionDao
import com.studypulse.app.data.local.entity.StudySessionEntity

@Database(
    entities     = [StudySessionEntity::class],
    version      = 1,
    exportSchema = true
)
abstract class StudyDatabase : RoomDatabase() {
    abstract fun studySessionDao(): StudySessionDao
}
