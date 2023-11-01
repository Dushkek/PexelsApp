package com.adush.pexelsapp.domain.usecases

import com.adush.pexelsapp.domain.ImageRepository
import com.adush.pexelsapp.domain.model.ImageItem
import javax.inject.Inject

class AddImageToBookmarksUseCase @Inject constructor(
    private val imageRepository: ImageRepository
) {

    operator fun invoke(imageItem: ImageItem) = imageRepository.addImageItemToDb(imageItem)

}