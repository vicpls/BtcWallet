package com.exmpl.btcwallet.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.exmpl.btcwallet.model.IUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HistoryVM
@Inject constructor(private val useCases: IUseCases) : ViewModel() {

    val flow = Pager(
        // Configure how data is loaded by passing additional properties to
        // PagingConfig, such as prefetchDistance.
        PagingConfig(pageSize = 25, prefetchDistance = 10, maxSize = 50)
    ) {
        HistoryPagingSource(useCases::getHistory)
    }.flow
        .cachedIn(viewModelScope)

}