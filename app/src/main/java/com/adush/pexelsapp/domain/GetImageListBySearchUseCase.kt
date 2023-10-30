package com.adush.pexelsapp.domain

import javax.inject.Inject

class GetImageListBySearchUseCase @Inject constructor(
    private val imageRepository: ImageRepository
) {

    operator fun invoke(query: String) = imageRepository.getImageListBySearch(query)

}