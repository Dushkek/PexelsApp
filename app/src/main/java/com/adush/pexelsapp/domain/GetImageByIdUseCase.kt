package com.adush.pexelsapp.domain

import javax.inject.Inject

class GetImageByIdUseCase @Inject constructor(
    private val imageRepository: ImageRepository
) {

    operator fun invoke(id: Int) = imageRepository.getImageById(id)
}