package dev.najihhome.pokekuy.presentation.auth.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.najihhome.pokekuy.domain.usecase.RegisterUserUseCase
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val registerUserUseCase: RegisterUserUseCase
) : ViewModel() {
    
    private val _registerState = MutableLiveData<RegisterState>(RegisterState())
    val registerState: LiveData<RegisterState> = _registerState
    
    fun register(username: String, email: String, password: String, confirmPassword: String) {
        _registerState.value = RegisterState(isLoading = true)
        
        viewModelScope.launch {
            val result = registerUserUseCase(username, email, password, confirmPassword)
            
            result.fold(
                onSuccess = {
                    _registerState.value = RegisterState(isSuccess = true)
                },
                onFailure = { exception ->
                    _registerState.value = RegisterState(error = exception.message ?: "Registration failed")
                }
            )
        }
    }
    
    data class RegisterState(
        val isLoading: Boolean = false,
        val isSuccess: Boolean = false,
        val error: String? = null
    )
}
