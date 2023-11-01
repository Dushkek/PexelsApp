package com.adush.pexelsapp.domain.usecases

import com.adush.pexelsapp.domain.ImageRepository
import javax.inject.Inject

class GetImageListFromDbUseCase @Inject constructor(
    private val imageRepository: ImageRepository
) {

    operator fun invoke() = imageRepository.getImageListFromDb()

}