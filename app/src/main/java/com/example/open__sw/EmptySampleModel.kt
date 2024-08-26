package com.example.open__sw

import alirezat775.lib.carouselview.CarouselModel

class EmptySampleModel (private val text: String) : CarouselModel() {

    fun getText(): String {
        return text
    }
}