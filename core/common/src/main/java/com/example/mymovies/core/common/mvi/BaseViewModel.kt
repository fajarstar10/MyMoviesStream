package com.example.mymovies.core.common.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 * Base ViewModel untuk arsitektur MVI
 */
abstract class BaseViewModel<S : MviState, I : MviIntent, E : MviEffect> : ViewModel() {

    private val initialState: S by lazy { createInitialState() }
    abstract fun createInitialState(): S

    private val _uiState = MutableStateFlow(initialState)
    val uiState = _uiState.asStateFlow()

    private val _intent = MutableSharedFlow<I>()
    
    private val _effect = Channel<E>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        subscribeIntents()
    }

    private fun subscribeIntents() {
        viewModelScope.launch {
            _intent.collect {
                handleIntent(it)
            }
        }
    }

    /**
     * Mengirim intent dari UI
     */
    fun setIntent(intent: I) {
        viewModelScope.launch { _intent.emit(intent) }
    }

    /**
     * Menangani intent yang masuk
     */
    abstract fun handleIntent(intent: I)

    /**
     * Memperbarui state UI
     */
    protected fun setState(reduce: S.() -> S) {
        val newState = _uiState.value.reduce()
        _uiState.value = newState
    }

    /**
     * Mengirim side effect satu kali
     */
    protected fun setEffect(builder: () -> E) {
        val effectValue = builder()
        viewModelScope.launch { _effect.send(effectValue) }
    }
}
