package dev.najihhome.pokekuy.presentation.auth.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.najihhome.pokekuy.domain.usecase.LoginUserUseCase
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginUserUseCase: LoginUserUseCase
) : ViewModel() {
    
    private val _loginState = MutableLiveData<LoginState>(LoginState())
    val loginState: LiveData<LoginState> = _loginState
    
    fun login(username: String, password: String) {
        _loginState.value = LoginState(isLoading = true)
        
        viewModelScope.launch {
            val result = loginUserUseCase(username, password)
            
            result.fold(
                onSuccess = {
                    _loginState.value = LoginState(isSuccess = true)
                },
                onFailure = { exception ->
                    _loginState.value = LoginState(error = exception.message ?: "Login failed")
                }
            )
        }
    }
    
    data class LoginState(
        val isLoading: Boolean = false,
        val isSuccess: Boolean = false,
        val error: String? = null
    )
}
