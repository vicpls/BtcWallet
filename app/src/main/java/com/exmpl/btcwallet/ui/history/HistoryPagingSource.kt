package com.exmpl.btcwallet.ui.history

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.exmpl.btcwallet.model.TransactionViewInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList

class HistoryPagingSource(
    val requester: (String?) -> Flow<TransactionViewInfo>
) : PagingSource<String, TransactionViewInfo>()
{
    override suspend fun load(
        params: LoadParams<String>
    ): LoadResult<String, TransactionViewInfo> {
        return try {
            val nextPageNumber = params.key // если null - первичный запрос
            val response = requester(nextPageNumber).toList()

            LoadResult.Page(
                data = response,
                prevKey = null, // Only paging forward.
                nextKey = if (response.size < params.loadSize) null
                            else response.last().txId
            )
        } catch (ex: Exception) {
            LoadResult.Error(ex)
        }
    }

    override fun getRefreshKey(state: PagingState<String, TransactionViewInfo>): String? {
        val anchorPosition = state.anchorPosition ?: return null
        val anchorPage = state.closestPageToPosition(anchorPosition) ?: return null
        return anchorPage.data.first().txId
    }
}