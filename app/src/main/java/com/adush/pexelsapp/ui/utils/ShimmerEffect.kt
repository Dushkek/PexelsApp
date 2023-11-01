package com.adush.pexelsapp.ui.utils

import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerDrawable

object ShimmerEffect {

    fun getShimmerDrawable(): ShimmerDrawable{
        val shimmer = Shimmer.AlphaHighlightBuilder()
            .setDuration(1000)
            .setBaseAlpha(0.8f)
            .setHighlightAlpha(0.6f)
            .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
            .setAutoStart(true)
            .build()

        return ShimmerDrawable().apply {
            setShimmer(shimmer)
        }
    }
}