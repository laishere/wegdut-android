package com.wegdut.wegdut

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.text.HtmlCompat
import com.wegdut.wegdut.data.edu.course.Course
import com.wegdut.wegdut.room.v1.edu.course.CourseDao
import com.wegdut.wegdut.room.v1.kv_storage.KVDao
import com.wegdut.wegdut.room.v1.kv_storage.KVEntity
import com.wegdut.wegdut.ui.course_table.CourseTableActivity
import com.wegdut.wegdut.utils.ApiUtils
import com.wegdut.wegdut.utils.CourseUtils
import com.wegdut.wegdut.utils.DateUtils
import dagger.android.DaggerService
import kotlinx.coroutines.*
import java.util.*
import javax.inject.Inject

/**
 * CourseService有多渠道启动，比如CourseActivity和AlarmManger。
 */
class CourseService : DaggerService() {

    @Inject
    lateinit var courseDao: CourseDao

    @Inject
    lateinit var kvDao: KVDao
    private lateinit var sharedPreferences: SharedPreferences

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        sharedPreferences = getSharedPreferences(packageName, MODE_PRIVATE)
        if (!isCourseNotificationEnabled()) {
            stopSelf()
            return
        }
        createNotificationChannel()
        try {
            CoroutineScope(Dispatchers.IO).launch {
                val minute = 60 * 1000L
                var lastCourse: Course? = null
                while (isActive) {
                    val firstCourse = getFirstCourse()
                    if (firstCourse == null || lastCourse == firstCourse || !isTaskValid(firstCourse)) {
                        MyLog.debug(this@CourseService, "没有课程或者任务重复")
                        delay(5 * minute) // 5分钟
                        continue
                    }
                    val timeAhead = COURSE_NOTIFICATION_MINUTES_AHEAD * minute
                    val timeToWait = firstCourse.start.time - System.currentTimeMillis() - timeAhead
                    if (timeToWait > 0)
                        delay(timeToWait)
                    val courses = getCoursesToNotify()
                    if (courses.isEmpty()) continue
                    val firstCourse2 = courses.first()
                    if (!isTaskValid(firstCourse2)) continue
                    notify(courses)
                    val lastTask =
                        KVEntity(COURSE_LAST_TASK, ApiUtils.gson.toJson(firstCourse2), Date())
                    kvDao.save(lastTask)
                    lastCourse = firstCourse2
                }
            }
        } catch (e: Throwable) {
            MyLog.error(this, e)
        }
    }

    /**
     * 检查任务是否重复
     */
    private fun isTaskValid(course: Course): Boolean {
        val kv = kvDao.get(COURSE_LAST_TASK) ?: return true
        val lastCourse = ApiUtils.gson.fromJson(kv.value, Course::class.java)
        if (lastCourse != course) return true
        val now = System.currentTimeMillis()
        val minute = 60_000L
        return now - kv.updatedAt.time > COURSE_NOTIFICATION_MINUTES_AHEAD * minute
    }

    private fun isCourseNotificationEnabled(): Boolean {
        return sharedPreferences.getBoolean(COURSE_NOTIFICATION_KEY, false)
    }

    private fun getTodayAndTomorrowCourses(): List<Course> {
        val today = DateUtils.onlyDate(Date())
        val courses = mutableListOf<Course>()
        courseDao.get(today)?.let { courses.addAll(it.course) }
        val calendar = Calendar.getInstance()
        calendar.time = today
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        val tomorrow = calendar.time
        courseDao.get(tomorrow)?.let { courses.addAll(it.course) }
        return courses
    }

    private fun getFirstCourse(): Course? {
        val courses = getTodayAndTomorrowCourses()
        val now = Date()
        return courses.filter { it.start.after(now) }.minByOrNull { it.start }
    }

    private fun getCoursesToNotify(): List<Course> {
        val minute = 60 * 1000L
        val now = System.currentTimeMillis()
        val maxStartTime = now + minute * COURSE_NOTIFICATION_MINUTES_AHEAD
        return getTodayAndTomorrowCourses().filter { it.start.time in now..maxStartTime }
            .sortedBy { it.start }
    }

    private fun notify(courses: List<Course>) {
        if (!isCourseNotificationEnabled()) {
            stopSelf()
            return
        }
        val template = getString(R.string.course_notification_template)
        val style = NotificationCompat.InboxStyle()
        courses.forEach {
            val text = template.format(it.name, it.location, CourseUtils.getTime(it))
            val html = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_COMPACT)
            style.addLine(html)
        }
        val intent = Intent(this, CourseTableActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val contentIntent =
            PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT)
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_wegdut_logo)
            .setContentTitle("${courses.size}门课程即将开始")
            .setStyle(style)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentIntent(contentIntent)
            .build()
        with(NotificationManagerCompat.from(this)) {
            notify(COURSE_NOTIFICATION_ID, notification)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.course_channel_name)
            val descriptionText = getString(R.string.course_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "COURSE"
        const val COURSE_NOTIFICATION_ID = 1
        const val COURSE_NOTIFICATION_KEY = "COURSE_NOTIFICATION_KEY"
        const val COURSE_NOTIFICATION_MINUTES_AHEAD = 30
        const val COURSE_LAST_TASK = "COURSE_LAST_TASK"
    }
}