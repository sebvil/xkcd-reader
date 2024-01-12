package com.colibrez.xkcdreader.android.ui.components.images

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.toSize
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter

@Composable
fun ZoomableImage(
    imageUrl: String,
    contentDescription: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    onSuccess: ((AsyncImagePainter.State.Success) -> Unit)? = {},
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var displayedImageSize by remember { mutableStateOf(Size(width = 0f, height = 0f)) }
    var intrinsicImageSize by remember { mutableStateOf(Size(width = 0f, height = 0f)) }

    AsyncImage(
        model = imageUrl,
        modifier = modifier
            .pointerInput(Unit) {
                detectTransformGestures(
                    onGesture = { centroid, pan, gestureZoom, _ ->
                        val oldScale = scale
                        val newScale = (scale * gestureZoom).coerceIn(
                            minimumValue = 1f,
                            maximumValue = MAX_ZOOM,
                        )
                        val newOffset = (
                            Offset(x = offset.x, y = offset.y) +
                                centroid / oldScale -
                                (centroid / newScale + pan / oldScale)
                            )

                        scale = newScale
                        val actualSize =
                            calculateActualSize(intrinsicImageSize, displayedImageSize, scale)
                        offset = calculateOffset(
                            intrinsicImageSize = intrinsicImageSize,
                            displayedImageSize = displayedImageSize,
                            actualImageSize = actualSize,
                            scale = scale,
                            offset = newOffset,
                        )
                    },
                )
            }
            .let { m -> onClick?.let { m.clickable(onClick = it) } ?: m }
            .onSizeChanged { displayedImageSize = it.toSize() }
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                translationX = -offset.x * scale
                translationY = -offset.y * scale
                transformOrigin = TransformOrigin(0f, 0f)
            },
        contentDescription = contentDescription,
        onSuccess = {
            intrinsicImageSize = it.painter.intrinsicSize
            onSuccess?.invoke(it)
        },
    )
}

private const val MAX_ZOOM = 10f

private fun calculateActualSize(
    intrinsicImageSize: Size,
    displayedImageSize: Size,
    scale: Float
): Size {
    val ratio = if (
        intrinsicImageSize.height / intrinsicImageSize.width
        <= displayedImageSize.height / displayedImageSize.width
    ) {
        displayedImageSize.width / intrinsicImageSize.width
    } else {
        displayedImageSize.height / intrinsicImageSize.height
    }
    return intrinsicImageSize * ratio * scale
}

private fun calculateOffset(
    intrinsicImageSize: Size,
    displayedImageSize: Size,
    actualImageSize: Size,
    scale: Float,
    offset: Offset
): Offset {
    return Offset(
        calculateXOffset(
            intrinsicImageSize = intrinsicImageSize,
            displayedImageSize = displayedImageSize,
            actualWidth = actualImageSize.width,
            scale = scale,
            offset = offset,
        ),
        calculateYOffset(
            intrinsicImageSize = intrinsicImageSize,
            displayedImageSize = displayedImageSize,
            actualHeight = actualImageSize.height,
            scale = scale,
            offset = offset,
        ),
    )
}

private fun calculateXOffset(
    intrinsicImageSize: Size,
    displayedImageSize: Size,
    actualWidth: Float,
    scale: Float,
    offset: Offset
): Float {
    val maxOffsetX = if (
        intrinsicImageSize.width / intrinsicImageSize.height
        >= displayedImageSize.width / displayedImageSize.height
    ) {
        (actualWidth - displayedImageSize.width) / scale
    } else {
        (displayedImageSize.width * (scale - 2) + actualWidth) / (2 * scale)
    }

    val minOffsetX = if (
        intrinsicImageSize.width / intrinsicImageSize.height
        >= displayedImageSize.width / displayedImageSize.height
    ) {
        0f
    } else {
        (displayedImageSize.width * scale - actualWidth) / (2 * scale)
    }
    return if (actualWidth > displayedImageSize.width) {
        offset.x.coerceIn(
            minimumValue = minOffsetX,
            maximumValue = maxOffsetX,
        )
    } else {
        (displayedImageSize.width * scale - displayedImageSize.width) / (2 * scale)
    }
}

private fun calculateYOffset(
    intrinsicImageSize: Size,
    displayedImageSize: Size,
    actualHeight: Float,
    scale: Float,
    offset: Offset
): Float {
    val maxOffsetY = if (
        intrinsicImageSize.height / intrinsicImageSize.width
        >= displayedImageSize.height / displayedImageSize.width
    ) {
        (actualHeight - displayedImageSize.height) / scale
    } else {
        (displayedImageSize.height * (scale - 2) + actualHeight) / (2 * scale)
    }

    val minOffsetY = if (
        intrinsicImageSize.height / intrinsicImageSize.width
        >= displayedImageSize.height / displayedImageSize.width
    ) {
        0f
    } else {
        (displayedImageSize.height * scale - actualHeight) / (2 * scale)
    }
    return if (actualHeight > displayedImageSize.height) {
        offset.y.coerceIn(
            minimumValue = minOffsetY,
            maximumValue = maxOffsetY,
        )
    } else {
        (displayedImageSize.height * scale - displayedImageSize.height) / (2 * scale)
    }
}
