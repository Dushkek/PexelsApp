package com.adush.pexelsapp.data.mapper

import com.adush.pexelsapp.data.database.model.ImageSrcDbModel
import com.adush.pexelsapp.data.network.model.collection.FeatureCollectionDto
import com.adush.pexelsapp.data.network.model.image.ImageItemDto
import com.adush.pexelsapp.data.network.model.image.ImageSrcDto
import com.adush.pexelsapp.domain.model.FeatureCollection
import com.adush.pexelsapp.domain.model.ImageItem
import com.adush.pexelsapp.domain.model.ImageSrc
import javax.inject.Inject

class ImageMapper @Inject constructor(){

    fun mapCollectionDtoToEntity(featureCollectionDto: FeatureCollectionDto): FeatureCollection = FeatureCollection(
        id = featureCollectionDto.id,
        title = featureCollectionDto.title,
        mediaCount = featureCollectionDto.mediaCount,
        photosCount = featureCollectionDto.photosCount,
        videosCount = featureCollectionDto.videosCount
    )

    fun mapImageDtoToEntity(imageItemDto: ImageItemDto): ImageItem = ImageItem(
        id = imageItemDto.id,
        url = imageItemDto.url,
        imageSrc = mapSrcDtoToEntity(imageItemDto.imageSrc),
        name = imageItemDto.name,
        height = imageItemDto.height,
        width = imageItemDto.width
    )

    private fun mapSrcDtoToEntity(imageSrcDto: ImageSrcDto): ImageSrc = ImageSrc(
        original = imageSrcDto.original,
        large2x = imageSrcDto.large2x,
        large = imageSrcDto.large,
        medium = imageSrcDto.medium,
        small= imageSrcDto.small,
        portrait = imageSrcDto.portrait,
        landscape = imageSrcDto.landscape,
        tiny = imageSrcDto.tiny
    )

    private fun mapSrcDbModelToEntity(imageSrcDbModel: ImageSrcDbModel): ImageSrc = ImageSrc(
        original = imageSrcDbModel.original,
        large2x = imageSrcDbModel.large2x,
        large = imageSrcDbModel.large,
        medium = imageSrcDbModel.medium,
        small= imageSrcDbModel.small,
        portrait = imageSrcDbModel.portrait,
        landscape = imageSrcDbModel.landscape,
        tiny = imageSrcDbModel.tiny
    )
}