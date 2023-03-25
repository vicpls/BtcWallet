package com.exmpl.btcwallet.ui.history

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.exmpl.btcwallet.R
import com.exmpl.btcwallet.databinding.HistoryItemBinding
import com.exmpl.btcwallet.model.TransactionViewInfo


class TxInfoAdapter(
    diffCB: DiffUtil.ItemCallback<TransactionViewInfo>) :
    PagingDataAdapter<TransactionViewInfo, TxInfoAdapter.TxInfoVHolder>(diffCB)
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TxInfoVHolder =
        TxInfoVHolder(
            HistoryItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )


    override fun onBindViewHolder(holder: TxInfoVHolder, position: Int) {
        val item = getItem(position)
        with(holder){
            txIdView.text = item?.txId ?: "xxx...xxx"
            amountView.text = item?.btc ?: "XXX"
            timeView.text = item?.time ?: "XXXX-XX-XX"

            setImage(item?.isSpending, signImg)
        }
    }

    private fun setImage(isSpending: Boolean?, img: ImageView){

        with(img) {
            if (isSpending != null) {
                if (isSpending) {
                    imageTintList = stateListRed
                    setImageResource(R.drawable.checkbox_marked_circle_minus_outline)
                } else {
                    imageTintList = stateListGreen
                    setImageResource(R.drawable.checkbox_marked_circle_plus_outline)
                }
            }else{
                imageTintList = null
                setImageResource(R.drawable.help_circle_outline)
            }
        }
    }


    inner class TxInfoVHolder(binding: HistoryItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val txIdView: TextView = binding.txId
        val amountView: TextView = binding.amount
        val timeView: TextView = binding.time
        val signImg: ImageView = binding.sign
    }

    companion object {
        private val colorsGreen = intArrayOf(Color.GREEN)
        private val colorsRed = intArrayOf(Color.RED)

        val states = arrayOf(
            intArrayOf(android.R.attr.state_enabled), // enabled
            /*intArrayOf(-android.R.attr.state_enabled), // disabled
            intArrayOf(-android.R.attr.state_checked), // unchecked
            intArrayOf(android.R.attr.state_pressed)  // pressed*/
        )

        private val stateListGreen = ColorStateList(states, colorsGreen)
        private val stateListRed = ColorStateList(states, colorsRed)
    }
}