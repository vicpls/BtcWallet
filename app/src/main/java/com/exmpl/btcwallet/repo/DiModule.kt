package com.exmpl.btcwallet.repo

import com.exmpl.btcwallet.repo.localstore.PrefStore
import com.exmpl.btcwallet.repo.testapi.Esplora
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DiModule {

    @Binds
    abstract fun bindLocStore(store: PrefStore) : ILocalStore

    @Binds
    abstract fun bindBtcApi(api: Esplora) : IbtcApi
}