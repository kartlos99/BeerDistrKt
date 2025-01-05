package com.example.beerdistrkt.fragPages.addEditUser

import com.example.beerdistrkt.fragPages.login.models.UserType
import com.example.beerdistrkt.fragPages.user.domain.UserValidationResult
import com.example.beerdistrkt.fragPages.user.domain.UserValidator
import com.example.beerdistrkt.fragPages.user.domain.model.User
import com.example.beerdistrkt.models.UserStatus
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test

class UserValidatorTest {

    @Before
    fun setUp() {
    }

    @Test
    fun `everything filled well returns success result`() {
        val user = User(
            "2", "username", "name", UserType.DISTRIBUTOR, "", "", "", "", UserStatus.ACTIVE, listOf()
        )
        val validator = UserValidator(user, false, "", "")

        assertEquals(UserValidationResult.Success, validator.validate())
    }

    @Test
    fun `no password on new user returns invalid password error result`() {
        val user = User(
            "", "username", "name", UserType.DISTRIBUTOR, "", "", "", "", UserStatus.ACTIVE, listOf()
        )
        val validator = UserValidator(user, false, "", "")

        assert(validator.validate() is UserValidationResult.Error)
        assert((validator.validate() as UserValidationResult.Error).errors.contains(
            UserValidationResult.ErrorType.InvalidPassword))
    }

    @Test
    fun `password not match returns invalid password error result`() {
        val user = User(
            "2", "username", "name", UserType.DISTRIBUTOR, "", "", "", "", UserStatus.ACTIVE, listOf()
        )
        val validator = UserValidator(user, true, "456", "678")

        assert(validator.validate() is UserValidationResult.Error)
        assert((validator.validate() as UserValidationResult.Error).errors.contains(
            UserValidationResult.ErrorType.PasswordNotMatch))
    }

    @Test
    fun `invalid user name returns invalid username included error list result`() {
        val user = mockk<User>(relaxed = true)
        every { user.username }.returns("ab")
        val validator = UserValidator(user, false, "456", "678")

        assert(validator.validate() is UserValidationResult.Error)
        assert((validator.validate() as UserValidationResult.Error).errors.contains(
            UserValidationResult.ErrorType.InvalidUsername))
    }

}