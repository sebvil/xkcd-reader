package com.colibrez.xkcdreader.data.model

import com.colibrez.xkcdreader.database.model.ComicEntity
import com.colibrez.xkcdreader.database.model.ComicInfo
import com.colibrez.xkcdreader.model.Comic
import com.colibrez.xkcdreader.network.model.NetworkComic
import com.colibrez.xkcdreader.network.model.XkcdNetworkComic


fun ComicInfo.asExternalModel() = Comic(
    num = num,
    title = title,
    transcript = transcript,
    img = img,
    alt = alt,
    link = link,
    year = year,
    month = month,
    day = day,
    isFavorite = isFavorite == 1L,
    isRead = isRead == 1L
)


fun Comic.asEntity(): ComicEntity = ComicEntity(
    num = num,
    title = title,
    transcript = transcript,
    img = img,
    alt = alt,
    link = link,
    year = year,
    month = month,
    day = day
)

fun NetworkComic.asEntity() = ComicEntity(
    num = num,
    title = title,
    transcript = transcript,
    img = img,
    alt = alt,
    link = link,
    year = year,
    month = month,
    day = day
)

fun Comic.asNetworkComic() = NetworkComic(
    num = num,
    title = title,
    transcript = transcript,
    img = img,
    alt = alt,
    link = link,
    year = year,
    month = month,
    day = day,
    isFavorite = isFavorite,
    isRead = isRead
)

fun XkcdNetworkComic.asEntity() = ComicEntity(
    num = num,
    title = title,
    transcript = transcript,
    img = img,
    alt = alt,
    link = link,
    year = year,
    month = month,
    day = day
)