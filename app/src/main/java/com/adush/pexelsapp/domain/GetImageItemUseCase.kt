package com.adush.pexelsapp.domain

import javax.inject.Inject

class GetImageItemUseCase @Inject constructor(
    private val imageRepository: ImageRepository
) {

    operator fun invoke(id: Int) = imageRepository.getImageItem(id)
}