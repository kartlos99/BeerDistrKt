package com.example.beerdistrkt.fragPages.user.domain.usecase

import com.example.beerdistrkt.fragPages.user.data.model.AddUserRequestModel
import com.example.beerdistrkt.fragPages.user.domain.UserRepository
import javax.inject.Inject

class PutUserUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(model: AddUserRequestModel) = userRepository.putUser(model)
}