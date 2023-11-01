package com.adush.pexelsapp.domain.usecases

import com.adush.pexelsapp.domain.ImageRepository
import javax.inject.Inject

class GetImageListUseCase @Inject constructor(
    private val imageRepository: ImageRepository
) {

    operator fun invoke() = imageRepository.getImageList()

}