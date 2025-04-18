package dev.najihhome.pokekuy.presentation.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dev.najihhome.pokekuy.R
import dev.najihhome.pokekuy.databinding.FragmentProfileBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileFragment : Fragment() {
    
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: ProfileViewModel by viewModel()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupClickListeners()
        observeViewModel()
        
        viewModel.loadUserData()
    }
    
    private fun setupClickListeners() {
        binding.buttonLogout.setOnClickListener {
            viewModel.logout()
        }
    }
    
    private fun observeViewModel() {
        viewModel.profileState.observe(viewLifecycleOwner) { state ->
            when {
                state.isLoading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.cardViewUserInfo.visibility = View.GONE
                    binding.textViewError.visibility = View.GONE
                }
                state.error != null -> {
                    binding.progressBar.visibility = View.GONE
                    Snackbar.make(binding.root, state.error, Snackbar.LENGTH_SHORT).show()
                }
                state.user != null -> {
                    binding.progressBar.visibility = View.GONE
                    binding.cardViewUserInfo.visibility = View.VISIBLE
                    binding.textViewError.visibility = View.GONE
                    
                    // Update UI with user data
                    binding.textViewUsername.text = state.user.username
                    binding.textViewEmail.text = state.user.email
                    binding.textViewJoinDate.text = "Joined: ${state.user.getFormattedCreatedAt()}"
                }
                state.isLoggedOut -> {
                    binding.progressBar.visibility = View.GONE
                    navigateToLogin()
                }
            }
        }
    }
    
    private fun navigateToLogin() {
        // Navigate to login screen and clear the back stack
        findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
