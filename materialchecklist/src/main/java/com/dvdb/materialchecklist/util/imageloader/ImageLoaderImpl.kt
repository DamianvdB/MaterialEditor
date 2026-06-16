/*
 * Designed and developed by Damian van den Berg.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dvdb.materialchecklist.util.imageloader

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import coil3.load
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import coil3.size.Scale

internal object ImageLoader {

    fun loadImage(
        target: ImageView,
        uri: Uri,
        onLoadSuccess: (Drawable?) -> Unit = {},
        onLoadFailed: () -> Unit = {}
    ) {
        target.scaleType = ImageView.ScaleType.CENTER_CROP
        target.load(uri) {
            placeholder(ColorDrawable(Color.TRANSPARENT))
            error(ColorDrawable(Color.TRANSPARENT))
            crossfade(true)
            scale(Scale.FILL)
            listener(
                onSuccess = { _, _ -> onLoadSuccess(target.drawable) },
                onError = { _, _ -> onLoadFailed() }
            )
        }
    }
}
