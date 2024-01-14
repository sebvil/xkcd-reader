package com.colibrez.xkcdreader.data.model

import com.colibrez.xkcdreader.database.model.ComicEntity
import com.colibrez.xkcdreader.database.model.ComicInfo
import com.colibrez.xkcdreader.model.Comic
import com.colibrez.xkcdreader.network.model.NetworkComic
import com.colibrez.xkcdreader.network.model.XkcdNetworkComic


fun ComicInfo.asExternalModel() = Comic(
    number = num,
    title = title,
    transcript = transcript,
    imageUrl = img,
    altText = alt,
    link = link,
    year = year,
    month = month,
    day = day,
    isFavorite = isFavorite == true,
    isRead = isRead == true
)


fun Comic.asEntity(): ComicEntity = ComicEntity(
    num = number,
    title = title,
    transcript = transcript,
    img = imageUrl,
    alt = altText,
    link = link,
    year = year,
    month = month,
    day = day,
    insertedTimestamp = 0
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
    day = day,
    insertedTimestamp = 0
)

fun Comic.asNetworkComic() = NetworkComic(
    num = number,
    title = title,
    transcript = transcript,
    img = imageUrl,
    alt = altText,
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
    day = day,
    insertedTimestamp = 0,
)