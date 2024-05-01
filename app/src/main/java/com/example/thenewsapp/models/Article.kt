package com.example.thenewsapp.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(
    tableName = "articles"
)
data class Article(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    val author: String = "",
    val content: String = "",
    val description: String = "",
    val publishedAt: String = "",
    val source: Source = Source("",""),
    val title: String = "",
    val url: String? = "",
    val urlToImage: String? = "" // Изменение типа на String? (может быть null)
) : Serializable {
    override fun hashCode(): Int {
        var result = id.hashCode()
        if (url != null && url.isNotEmpty()) {
            result = 31 * result + url.hashCode()
        }
        if (urlToImage != null && urlToImage.isNotEmpty()) {
            result = 31 * result + urlToImage.hashCode()
        }
        return result
    }
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Article
        if (id != other.id) return false
        if (author != other.author) return false
        if (content != other.content) return false
        if (description != other.description) return false
        if (publishedAt != other.publishedAt) return false
        if (source != other.source) return false
        if (title != other.title) return false
        if (url != other.url) return false
        if (urlToImage != other.urlToImage) return false
        return true
    }
}