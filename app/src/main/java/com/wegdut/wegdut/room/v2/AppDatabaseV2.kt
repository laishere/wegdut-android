package com.wegdut.wegdut.room.v2

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.wegdut.wegdut.room.v1.edu.TermDao
import com.wegdut.wegdut.room.v1.edu.TermEntity
import com.wegdut.wegdut.room.v1.edu.course.*
import com.wegdut.wegdut.room.v1.edu.exam_score.ExamScoreDao
import com.wegdut.wegdut.room.v1.edu.exam_score.ExamScoreEntity
import com.wegdut.wegdut.room.v1.kv_storage.KVDao
import com.wegdut.wegdut.room.v1.kv_storage.KVEntity
import com.wegdut.wegdut.room.v1.news.NewsDao
import com.wegdut.wegdut.room.v1.news.NewsEntity

@Database(
    entities = [DayCourseEntity::class, CourseInfoEntity::class, TermEntity::class,
        KVEntity::class, HolidayEntity::class, CourseBreakEntity::class,
        NewsEntity::class, ExamScoreEntity::class], version = 2
)
abstract class AppDatabaseV2 : RoomDatabase() {
    abstract fun courseDao(): CourseDao
    abstract fun kvDao(): KVDao
    abstract fun courseBreakDao(): CourseBreakDao
    abstract fun holidayDao(): HolidayDao
    abstract fun newsDao(): NewsDao
    abstract fun courseInfoDao(): CourseInfoDao
    abstract fun termDao(): TermDao
    abstract fun examScoreDao(): ExamScoreDao

    companion object {
        private var INSTANCE: AppDatabaseV2? = null

        fun getInstance(context: Context): AppDatabaseV2 {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context, AppDatabaseV2::class.java, "wegdut-db")
                    .fallbackToDestructiveMigration()
                    .addMigrations(object : Migration(1, 2) {
                        override fun migrate(database: SupportSQLiteDatabase) {
                            // Course model变更，删除数据库数据
                            database.delete("course_info", null, null)
                            database.delete("course", null, null)
                        }
                    })
                    .build()
            }
            return INSTANCE!!
        }
    }
}