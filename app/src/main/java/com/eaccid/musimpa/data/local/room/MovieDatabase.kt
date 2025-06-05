package com.eaccid.musimpa.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [MovieEntity::class],
    version = 2
)
abstract class MovieDatabase : RoomDatabase() {
    abstract val movieDao: MovieDao
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE Movieentity ADD COLUMN page INT NOT NULL DEFAULT 0")
    }
}