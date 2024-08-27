package com.example.open__sw

import alirezat775.lib.carouselview.CarouselModel

data class SampleModel(
    val title: String,
    val summary: String,
    val newsURL: String,
    val imgURL: String,
    var islikeFilled: Boolean = false
) : CarouselModel() {
    // 기존 id 속성 제거, 사용하지 않으므로 제거
}
