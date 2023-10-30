package com.adush.pexelsapp.domain

import javax.inject.Inject

class GetFeaturedCollectionsUseCase @Inject constructor(
    private val imageRepository: ImageRepository
) {

    operator fun invoke() = imageRepository.getFeaturedCollections()

}