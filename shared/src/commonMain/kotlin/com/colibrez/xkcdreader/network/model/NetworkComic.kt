package com.colibrez.xkcdreader.network.model

import com.colibrez.xkcdreader.model.Comic
import kotlinx.serialization.Serializable

/**
 * Network representation of [Comic].
 */
@Serializable
data class NetworkComic(
    val num: Long,
    val title: String,
    val transcript: String,
    val img: String,
    val alt: String,
    val link: String,
    val year: Long,
    val month: Long,
    val day: Long,
    val isFavorite: Boolean,
    val isRead: Boolean
)
