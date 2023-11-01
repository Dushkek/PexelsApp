package com.adush.pexelsapp.data.mapper

import com.adush.pexelsapp.data.database.model.BookmarksImageItemDbModel
import com.adush.pexelsapp.data.database.model.BookmarksImageSrcDbModel
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
        author = imageItemDto.author,
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

    fun mapBookmarkImageDbToEntity(bookmarksImageItemDbModel: BookmarksImageItemDbModel): ImageItem = ImageItem(
        id = bookmarksImageItemDbModel.id,
        url = bookmarksImageItemDbModel.url,
        author = bookmarksImageItemDbModel.author,
        imageSrc = mapSrcDbModelToEntity(bookmarksImageItemDbModel.imageSrc),
        name = bookmarksImageItemDbModel.name,
        height = bookmarksImageItemDbModel.height,
        width = bookmarksImageItemDbModel.width,
        bookmark = bookmarksImageItemDbModel.bookmark
    )

    private fun mapSrcDbModelToEntity(bookmarksImageSrcDbModel: BookmarksImageSrcDbModel): ImageSrc = ImageSrc(
        original = bookmarksImageSrcDbModel.original,
        large2x = bookmarksImageSrcDbModel.large2x,
        large = bookmarksImageSrcDbModel.large,
        medium = bookmarksImageSrcDbModel.medium,
        small= bookmarksImageSrcDbModel.small,
        portrait = bookmarksImageSrcDbModel.portrait,
        landscape = bookmarksImageSrcDbModel.landscape,
        tiny = bookmarksImageSrcDbModel.tiny
    )

    fun mapEntityToBookmarkImageDb(imageItem: ImageItem): BookmarksImageItemDbModel = BookmarksImageItemDbModel(
        id = imageItem.id,
        url = imageItem.url,
        author = imageItem.author,
        imageSrc = mapSrcEntityToDbModel(imageItem.imageSrc),
        name = imageItem.name,
        height = imageItem.height,
        width = imageItem.width,
        bookmark = imageItem.bookmark
    )

    private fun mapSrcEntityToDbModel(imageSrc: ImageSrc): BookmarksImageSrcDbModel = BookmarksImageSrcDbModel(
        original = imageSrc.original,
        large2x = imageSrc.large2x,
        large = imageSrc.large,
        medium = imageSrc.medium,
        small= imageSrc.small,
        portrait = imageSrc.portrait,
        landscape = imageSrc.landscape,
        tiny = imageSrc.tiny
    )
}