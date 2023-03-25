package com.exmpl.btcwallet.ui.history

import androidx.recyclerview.widget.DiffUtil
import com.exmpl.btcwallet.model.TransactionViewInfo

object TxInfoComparator : DiffUtil.ItemCallback<TransactionViewInfo>() {

    override fun areItemsTheSame(oldItem: TransactionViewInfo, newItem: TransactionViewInfo): Boolean {
        // Id is unique.
        return oldItem.txId == newItem.txId
    }

    override fun areContentsTheSame(oldItem: TransactionViewInfo, newItem: TransactionViewInfo): Boolean {
        return oldItem == newItem
    }
}