package com.wegdut.wegdut.di

import android.app.Application
import com.wegdut.wegdut.MyApplication
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [AppModule::class,
        FragmentBindingModule::class,
        ActivityBindingModule::class,
        AndroidSupportInjectionModule::class,
        DatabaseModule::class,
        ApiModule::class,
        StatelessRepositoryModule::class,
        ServiceBindingModule::class
    ]
)
interface AppComponent : AndroidInjector<MyApplication> {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder
        fun build(): AppComponent
    }
}