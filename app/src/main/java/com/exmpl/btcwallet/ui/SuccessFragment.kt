package com.exmpl.btcwallet.ui

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.exmpl.btcwallet.R
import com.exmpl.btcwallet.databinding.SuccessFragmentBinding

/**
 * A [Fragment] with success result.
 */
class SuccessFragment : Fragment() {

    private var _binding: SuccessFragmentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    val viewModel : WalletViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = SuccessFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.tvTxId.apply{
            setMovementMethod( LinkMovementMethod.getInstance())
            text = viewModel.getHtml()
        };

        binding.tvTransactionFee.text = viewModel.transactionFee

        binding.btMore.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}