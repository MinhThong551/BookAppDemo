package com.example.bookappdemo.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bookappdemo.di.DaggerViewModelFactory
import com.example.bookappdemo.di.ViewModelKey
import com.example.bookappdemo.ui.listBook.FirestoreViewModel
import com.example.bookappdemo.ui.listBook.ListBookViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(ListBookViewModel::class)
    abstract fun bindListBookViewModel(viewModel: ListBookViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FirestoreViewModel::class)
    abstract fun bindFirestoreViewModel(viewModel: FirestoreViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: DaggerViewModelFactory): ViewModelProvider.Factory

}


