package com.meditrust.findadoctor.auth.presentation.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meditrust.findadoctor.auth.domain.usecase.LoginUseCase
import com.meditrust.findadoctor.auth.domain.usecase.RegisterUseCase
import com.meditrust.findadoctor.auth.presentation.state.AuthViewState
import com.meditrust.findadoctor.auth.presentation.state.AuthUiEvent
import com.meditrust.findadoctor.auth.presentation.state.AuthUiEffect
import kotlinx.coroutines.launch

class AuthViewModel(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _viewState = MutableLiveData(AuthViewState())
    val viewState: LiveData<AuthViewState> = _viewState

    private val _uiEffect = MutableLiveData<AuthUiEffect>()
    val uiEffect: LiveData<AuthUiEffect> = _uiEffect

    fun onEvent(event: AuthUiEvent) {
        when (event) {
            is AuthUiEvent.Login -> {
                viewModelScope.launch {
//                    val result = loginUseCase.execute(event.email, event.password)
                    // Handle result and update view state
                }
            }
            is AuthUiEvent.Register -> {
                viewModelScope.launch {
//                    val result = registerUseCase.execute(event.email, event.password)
                    // Handle result and update view state
                }
            }

            is AuthUiEvent.EmailChanged -> {}
            is AuthUiEvent.PasswordChanged -> {}
        }
    }
}
