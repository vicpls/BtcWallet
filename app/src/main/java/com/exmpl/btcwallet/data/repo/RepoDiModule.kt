package com.exmpl.btcwallet.data.repo

import com.exmpl.btcwallet.data.repo.localstore.PrefStore
import com.exmpl.btcwallet.data.repo.network.Esplora
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepoDiModule {

    @Binds
    abstract fun bindLocStore(store: PrefStore) : ILocalStore

    @Binds
    abstract fun bindBtcApi(api: Esplora) : IbtcApi
}