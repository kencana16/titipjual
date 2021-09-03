package com.kencana.titipjual.ui.auth

/**
 * Data validation state of the login form.
 */
data class LoginFormState(
    val phoneError: Int? = null,
    val passwordError: Int? = null,
    val isDataValid: Boolean = false
)
