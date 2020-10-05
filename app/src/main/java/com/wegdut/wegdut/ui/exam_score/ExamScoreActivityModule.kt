package com.wegdut.wegdut.ui.exam_score

import com.wegdut.wegdut.di.ActivityScoped
import dagger.Binds
import dagger.Module

@Module
interface ExamScoreActivityModule {
    @ActivityScoped
    @Binds
    fun presenter(presenter: ExamScoreActivityPresenter): ExamScoreActivityContract.Presenter
}