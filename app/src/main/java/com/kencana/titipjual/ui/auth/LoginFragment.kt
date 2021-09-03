package com.kencana.titipjual.ui.auth

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.kencana.titipjual.MainActivity
import com.kencana.titipjual.R
import com.kencana.titipjual.data.network.Resource
import com.kencana.titipjual.databinding.FragmentLoginBinding
import com.kencana.titipjual.utils.afterTextChanged
import com.kencana.titipjual.utils.handleApiError
import com.kencana.titipjual.utils.startNewActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/*
* AuthViewModel()
* ViewModelProvider
* */

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var binding: FragmentLoginBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLoginBinding.bind(view)

        val phone = binding.etPhone
        val password = binding.etPassword
        val login = binding.ibtnLogin
        val loading = binding.loading

        viewModel.loginFormState.observe(
            viewLifecycleOwner,
            Observer {
                val loginState = it ?: return@Observer

                // disable login button unless both username / password is valid
                login.isEnabled = loginState.isDataValid

                binding.tilPhone.error = loginState.phoneError?.let { it1 -> getString(it1) }
                binding.tilPassword.error = loginState.passwordError?.let { it1 -> getString(it1) }
            }
        )

        phone.afterTextChanged {
            viewModel.loginDataChanged(
                phone.text.toString(),
                password.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                viewModel.loginDataChanged(
                    phone.text.toString(),
                    password.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        viewModel.login(
                            phone.text.toString(),
                            password.text.toString()
                        )
                }
                false
            }

            login.setOnClickListener {
                loading.visibility = View.VISIBLE
                viewModel.login(phone.text.toString(), password.text.toString())
            }
        }

        viewModel.loginResponse.observe(
            viewLifecycleOwner,
            {
                loading.visibility = View.GONE
                Log.e(Companion.TAG, "loginResponse: $it")
                when (it) {
                    is Resource.Success -> {
                        val userId = it.value.data?.user?.idUser!!
                        val token = it.value.data.user.token!!
                        lifecycleScope.launch {
                            viewModel.saveAuthToken(userId, token)
                            requireActivity().startNewActivity(MainActivity::class.java)
                        }
                    }
                    is Resource.Failure -> handleApiError(it) {
                        viewModel.login(
                            phone.text.toString(),
                            password.text.toString()
                        )
                    }
                    else -> {}
                }
            }
        )
    }

    companion object {
        private const val TAG = "LoginFragment"
    }
}
