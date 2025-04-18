package dev.najihhome.pokekuy.presentation.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.najihhome.pokekuy.domain.model.User
import dev.najihhome.pokekuy.domain.usecase.GetCurrentUserUseCase
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {
    
    private val _profileState = MutableLiveData<ProfileState>(ProfileState())
    val profileState: LiveData<ProfileState> = _profileState
    
    fun loadUserData() {
        _profileState.value = ProfileState(isLoading = true)
        
        viewModelScope.launch {
            val result = getCurrentUserUseCase()
            
            result.fold(
                onSuccess = { user ->
                    _profileState.value = ProfileState(user = user)
                },
                onFailure = { exception ->
                    _profileState.value = ProfileState(error = exception.message ?: "Failed to load user data")
                }
            )
        }
    }
    
    fun logout() {
        _profileState.value = ProfileState(isLoading = true)
        
        viewModelScope.launch {
            val result = getCurrentUserUseCase.logoutUser()
            
            result.fold(
                onSuccess = {
                    _profileState.value = ProfileState(isLoggedOut = true)
                },
                onFailure = { exception ->
                    _profileState.value = ProfileState(error = exception.message ?: "Failed to logout")
                }
            )
        }
    }
    
    data class ProfileState(
        val isLoading: Boolean = false,
        val user: User? = null,
        val error: String? = null,
        val isLoggedOut: Boolean = false
    )
}
