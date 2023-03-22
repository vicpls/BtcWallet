package com.exmpl.btcwallet.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.exmpl.btcwallet.R
import com.exmpl.btcwallet.databinding.BalanceFragmentBinding
import com.exmpl.btcwallet.model.Result
import com.google.android.material.snackbar.Snackbar
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
            viewModel.send(amount, address)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.trResult.collect{
                    when (it) {
                        is Result.SUCCESS<*,*> -> {
                            showProgressBar(false)
                            binding.btSend.isEnabled = true
                            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
                            viewModel.updateBalance()
                            }
                        is Result.ERORR -> {
                            showProgressBar(false)
                            Snackbar.make(
                                binding.guideline,
                                it.description,
                                Snackbar.LENGTH_LONG
                            ).show()
                            }
                        is Result.INPROCESS -> {
                            showProgressBar(true)
                            binding.btSend.isEnabled = false
                        }
                        is Result.NOP -> {
                            showProgressBar(false)
                            binding.btSend.isEnabled = true
                        }
                    }
                }
            }
        }

        editTextValidationSet()
    }


    fun editTextValidationSet() {
        binding.etAmount.editText?.doOnTextChanged { text, _, _, _ ->
            text ?: return@doOnTextChanged

            binding.etAmount.error =
                if (!viewModel.isSpentSumCorrect(text.toString()))
                    getString(R.string.over_limit)
                else
                    null
        }
    }

    fun showProgressBar(isInProcess: Boolean){
        binding.progressBar.visibility =
            if (isInProcess) VISIBLE else INVISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}