package dev.najihhome.pokekuy.presentation.auth.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dev.najihhome.pokekuy.R
import dev.najihhome.pokekuy.databinding.FragmentLoginBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : Fragment() {
    
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: LoginViewModel by viewModel()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupClickListeners()
        observeViewModel()
    }
    
    private fun setupClickListeners() {
        binding.buttonLogin.setOnClickListener {
            val username = binding.editTextUsername.text.toString().trim()
            val password = binding.editTextPassword.text.toString().trim()
            
            viewModel.login(username, password)
        }
        
        binding.textViewRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }
    
    private fun observeViewModel() {
        viewModel.loginState.observe(viewLifecycleOwner) { state ->
            when {
                state.isLoading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.buttonLogin.isEnabled = false
                }
                state.error != null -> {
                    binding.progressBar.visibility = View.GONE
                    binding.buttonLogin.isEnabled = true
                    Toast.makeText(requireContext(), state.error, Toast.LENGTH_SHORT).show()
                }
                state.isSuccess -> {
                    binding.progressBar.visibility = View.GONE
                    binding.buttonLogin.isEnabled = true
                    navigateToHome()
                }
            }
        }
    }
    
    private fun navigateToHome() {
        findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
