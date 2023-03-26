package com.exmpl.btcwallet.model

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class UseCaseDiModule {

    @Binds
    abstract fun bindUseCases(us: UseCases): IUseCases
}