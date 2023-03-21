package com.exmpl.btcwallet.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.exmpl.btcwallet.databinding.BalanceFragmentBinding
import kotlinx.coroutines.launch

/**
 * A [Fragment] show balance and creating transaction.
 */
class BalanceFragment : Fragment() {

    private var _binding: BalanceFragmentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    val viewModel : WalletViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = BalanceFragmentBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btSend.setOnClickListener {

            val amount = binding.etAmount.editText?.text.toString()
            val address = binding.etAddress.editText?.text.toString()

            viewModel.send(amount, address, requireActivity().applicationContext)
            //findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.fbalance.collect{
                    binding.tvBalance.text = it.toString()
                }
            }
        }



    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}