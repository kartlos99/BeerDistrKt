package com.example.beerdistrkt.fragPages.addEditUser

import com.example.beerdistrkt.models.User

class UserValidator(
    private val userData: User,
    private val isChangingPassword: Boolean,
    private val password: String,
    private val confirmPassword: String
) {

    companion object {
        const val MIN_USERNAME_LENGTH = 3
        const val MIN_NAME_LENGTH = 3
    }

    val result = UserValidationResult.Success
    private val errors = mutableListOf<UserValidationResult.ErrorType>()

    fun validate(): UserValidationResult {
        errors.clear()
        checkUsername()
        checkName()
        if (userData.id.isEmpty() || isChangingPassword) {
            checkPassword()
            checkPasswordMatch()
        }
        return if (errors.isEmpty())
            UserValidationResult.Success
        else
            UserValidationResult.Error(errors)
    }

    private fun checkUsername() {
        if (userData.username.trim().length < MIN_USERNAME_LENGTH)
            errors.add(UserValidationResult.ErrorType.InvalidUsername)
    }

    private fun checkName() {
        if (userData.name.trim().length < MIN_NAME_LENGTH)
            errors.add(UserValidationResult.ErrorType.InvalidName)
    }

    private fun checkPassword() {
        if (password.trim().length < 6)
            errors.add(UserValidationResult.ErrorType.InvalidPassword)
    }

    private fun checkPasswordMatch() {
        if (password != confirmPassword)
            errors.add(UserValidationResult.ErrorType.PasswordNotMatch)
    }
}

sealed class UserValidationResult {

    object Success : UserValidationResult()
    class Error(val errors: List<ErrorType>) : UserValidationResult()

    sealed class ErrorType {
        object InvalidUsername : ErrorType()
        object InvalidName : ErrorType()
        object InvalidPassword : ErrorType()
        object PasswordNotMatch : ErrorType()
    }
}