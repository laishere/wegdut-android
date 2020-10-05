package com.wegdut.wegdut.ui.news

import com.wegdut.wegdut.di.FragmentScoped
import com.wegdut.wegdut.ui.news.annotation.AnnotationFragment
import com.wegdut.wegdut.ui.news.annotation.AnnotationModule
import com.wegdut.wegdut.ui.news.newsletter.NewsletterFragment
import com.wegdut.wegdut.ui.news.newsletter.NewsletterModule
import com.wegdut.wegdut.ui.news.notification.NotificationFragment
import com.wegdut.wegdut.ui.news.notification.NotificationModule
import com.wegdut.wegdut.ui.news.tender.TenderFragment
import com.wegdut.wegdut.ui.news.tender.TenderModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface NewsModule {

    @FragmentScoped
    @ContributesAndroidInjector(modules = [NotificationModule::class])
    fun notification(): NotificationFragment

    @FragmentScoped
    @ContributesAndroidInjector(modules = [AnnotationModule::class])
    fun annotation(): AnnotationFragment

    @FragmentScoped
    @ContributesAndroidInjector(modules = [NewsletterModule::class])
    fun newsletter(): NewsletterFragment

    @FragmentScoped
    @ContributesAndroidInjector(modules = [TenderModule::class])
    fun tender(): TenderFragment
}