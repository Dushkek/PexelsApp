package com.adush.pexelsapp.domain

import com.adush.pexelsapp.domain.model.ImageItem
import javax.inject.Inject

class AddImageItemToBookmarksUseCase @Inject constructor(
    private val imageRepository: ImageRepository
) {

    operator fun invoke(imageItem: ImageItem) {
        imageRepository.addImageItem(imageItem)
    }
}