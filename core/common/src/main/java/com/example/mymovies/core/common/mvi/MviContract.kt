package com.example.mymovies.core.common.mvi

interface UiState
interface UiIntent
interface UiEffect

/**
 * Representasi State Tunggal untuk UI
 */
interface MviState : UiState

/**
 * Aksi pengguna atau event dari UI
 */
interface MviIntent : UiIntent

/**
 * Side effect satu kali (navigasi, toast, snackbar)
 */
interface MviEffect : UiEffect
