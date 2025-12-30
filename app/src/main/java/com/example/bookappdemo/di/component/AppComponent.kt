package com.example.bookappdemo.di.component

import com.example.bookappdemo.di.module.NetworkModule
import com.example.bookappdemo.di.module.ViewModelModule
import com.example.bookappdemo.ui.listBook.ListBookActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [NetworkModule::class, ViewModelModule::class])
interface AppComponent   {
    fun inject(activity: ListBookActivity)

}