package com.meditrust.findadoctor.auth.presentation.login.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.meditrust.findadoctor.auth.presentation.login.state.LoginUiEffect
import com.meditrust.findadoctor.auth.presentation.login.state.LoginUiEvent
import com.meditrust.findadoctor.auth.presentation.login.state.LoginViewState

class LoginViewModel : ViewModel() {
    private val _viewState = MutableLiveData(LoginViewState())
    val viewState: LiveData<LoginViewState> = _viewState

    private val _uiEffect = MutableLiveData<LoginUiEffect>()
    val uiEffect: LiveData<LoginUiEffect> = _uiEffect

    fun onEvent(event: LoginUiEvent) {
        when (event) {
            is LoginUiEvent.EmailChanged -> {
                _viewState.value = _viewState.value?.copy(email = event.email)
            }
            is LoginUiEvent.PasswordChanged -> {
                _viewState.value = _viewState.value?.copy(password = event.password)
            }
            is LoginUiEvent.Submit -> {
                // Handle submit logic
            }
        }
    }
}
