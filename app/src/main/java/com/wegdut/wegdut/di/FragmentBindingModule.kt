package com.wegdut.wegdut.di

import com.wegdut.wegdut.ui.email_register.EmailRegisterFragment
import com.wegdut.wegdut.ui.email_register.EmailRegisterModule
import com.wegdut.wegdut.ui.exam_score.ExamScoreFragment
import com.wegdut.wegdut.ui.exam_score.ExamScoreFragmentModule
import com.wegdut.wegdut.ui.home.HomeFragment
import com.wegdut.wegdut.ui.home.HomeModule
import com.wegdut.wegdut.ui.message.MessageFragment
import com.wegdut.wegdut.ui.message.MessageModule
import com.wegdut.wegdut.ui.message.like.LikeFragment
import com.wegdut.wegdut.ui.message.like.LikeModule
import com.wegdut.wegdut.ui.message.reply.ReplyFragment
import com.wegdut.wegdut.ui.message.reply.ReplyModule
import com.wegdut.wegdut.ui.message.system.SystemFragment
import com.wegdut.wegdut.ui.message.system.SystemModule
import com.wegdut.wegdut.ui.post.PostFragment
import com.wegdut.wegdut.ui.post.PostModule
import com.wegdut.wegdut.ui.student_register.StudentRegisterFragment
import com.wegdut.wegdut.ui.student_register.StudentRegisterModule
import com.wegdut.wegdut.ui.user.UserFragment
import com.wegdut.wegdut.ui.user.UserModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface FragmentBindingModule {
    @FragmentScoped
    @ContributesAndroidInjector(modules = [HomeModule::class])
    fun home(): HomeFragment

    @FragmentScoped
    @ContributesAndroidInjector(modules = [PostModule::class])
    fun explore(): PostFragment

    @FragmentScoped
    @ContributesAndroidInjector(modules = [MessageModule::class])
    fun message(): MessageFragment

    @FragmentScoped
    @ContributesAndroidInjector(modules = [ReplyModule::class])
    fun reply(): ReplyFragment

    @FragmentScoped
    @ContributesAndroidInjector(modules = [LikeModule::class])
    fun like(): LikeFragment

    @FragmentScoped
    @ContributesAndroidInjector(modules = [SystemModule::class])
    fun system(): SystemFragment

    @FragmentScoped
    @ContributesAndroidInjector(modules = [UserModule::class])
    fun user(): UserFragment

    @FragmentScoped
    @ContributesAndroidInjector(modules = [ExamScoreFragmentModule::class])
    fun examScore(): ExamScoreFragment

    @FragmentScoped
    @ContributesAndroidInjector(modules = [EmailRegisterModule::class])
    fun emailRegister(): EmailRegisterFragment

    @FragmentScoped
    @ContributesAndroidInjector(modules = [StudentRegisterModule::class])
    fun studentRegister(): StudentRegisterFragment
}