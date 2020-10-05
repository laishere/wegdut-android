package com.wegdut.wegdut.ui.exam_score

import com.wegdut.wegdut.data.edu.exam_score.ExamScoreRepository
import com.wegdut.wegdut.data.edu.exam_score.ExamScoreRepositoryImpl
import com.wegdut.wegdut.di.FragmentScoped
import dagger.Binds
import dagger.Module

@Module
interface ExamScoreFragmentModule {
    @FragmentScoped
    @Binds
    fun presenter(presenter: ExamScorePresenter): ExamScoreFragmentContract.Presenter

    @FragmentScoped
    @Binds
    fun repository(repository: ExamScoreRepositoryImpl): ExamScoreRepository
}