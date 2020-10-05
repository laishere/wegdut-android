package com.wegdut.wegdut.di

import com.wegdut.wegdut.ui.course_table.CourseTableActivity
import com.wegdut.wegdut.ui.course_table.CourseTableModule
import com.wegdut.wegdut.ui.exam_score.ExamScoreActivity
import com.wegdut.wegdut.ui.exam_score.ExamScoreActivityModule
import com.wegdut.wegdut.ui.login.LoginActivity
import com.wegdut.wegdut.ui.login.LoginModule
import com.wegdut.wegdut.ui.news.NewsActivity
import com.wegdut.wegdut.ui.news.NewsModule
import com.wegdut.wegdut.ui.post_details.PostDetailsActivity
import com.wegdut.wegdut.ui.post_details.PostDetailsModule
import com.wegdut.wegdut.ui.send_post.SendPostActivity
import com.wegdut.wegdut.ui.send_post.SendPostModule
import com.wegdut.wegdut.ui.setting.SettingActivity
import com.wegdut.wegdut.ui.setting.SettingModule
import com.wegdut.wegdut.ui.student_verification.StudentVerificationActivity
import com.wegdut.wegdut.ui.student_verification.StudentVerificationCheckActivity
import com.wegdut.wegdut.ui.student_verification.StudentVerificationModule
import com.wegdut.wegdut.ui.user_modification.UserModificationActivity
import com.wegdut.wegdut.ui.user_modification.UserModificationModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface ActivityBindingModule {
    @ActivityScoped
    @ContributesAndroidInjector(modules = [PostDetailsModule::class])
    fun exploreDetails(): PostDetailsActivity

    @ActivityScoped
    @ContributesAndroidInjector(modules = [CourseTableModule::class])
    fun course(): CourseTableActivity

    @ActivityScoped
    @ContributesAndroidInjector(modules = [NewsModule::class])
    fun schoolNotification(): NewsActivity

    @ActivityScoped
    @ContributesAndroidInjector(modules = [LoginModule::class])
    fun loginActivity(): LoginActivity

    @ActivityScoped
    @ContributesAndroidInjector(modules = [SettingModule::class])
    fun settingActivity(): SettingActivity

    @ActivityScoped
    @ContributesAndroidInjector(modules = [UserModificationModule::class])
    fun userModificationActivity(): UserModificationActivity

    @ActivityScoped
    @ContributesAndroidInjector(modules = [ExamScoreActivityModule::class])
    fun examScoreActivity(): ExamScoreActivity

    @ActivityScoped
    @ContributesAndroidInjector(modules = [SendPostModule::class])
    fun sendPostActivity(): SendPostActivity

    @ActivityScoped
    @ContributesAndroidInjector(modules = [StudentVerificationModule::class])
    fun studentVerification(): StudentVerificationActivity

    @ActivityScoped
    @ContributesAndroidInjector(modules = [StudentVerificationModule::class])
    fun studentVerificationCheck(): StudentVerificationCheckActivity
}