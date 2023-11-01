package com.adush.pexelsapp.domain.usecases

import com.adush.pexelsapp.domain.ImageRepository
import javax.inject.Inject

class GetFeaturedCollectionsUseCase @Inject constructor(
    private val imageRepository: ImageRepository
) {

    operator fun invoke() = imageRepository.getFeaturedCollections()

}