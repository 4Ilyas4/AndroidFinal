package com.example.thenewsapp.models

import java.io.Serializable

data class NewsResponse(
    var articles: MutableList<Article>,
    val status: String,
    val totalResults: Int
): Serializable