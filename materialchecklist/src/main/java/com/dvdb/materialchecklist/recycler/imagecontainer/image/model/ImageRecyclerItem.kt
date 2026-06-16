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

package com.dvdb.materialchecklist.recycler.imagecontainer.image.model

import android.graphics.drawable.Drawable
import android.net.Uri

internal data class ImageRecyclerItem(
    val id: Int,
    val text: String,
    val primaryImage: Drawable?,
    val primaryImageUri: Uri? = null,
    val secondaryImage: Drawable?,
    val secondaryImageUri: Uri? = null,
    val attachmentUri: Uri? = null,
    val attachmentMimeType: String = "",
    val attachmentDisplayName: String = "",
    val attachmentSizeLabel: String = "",
    val attachmentDateLabel: String = "",
    val attachmentActionLabel: String = ""
) {
    val displayText: String = attachmentDisplayName.ifBlank { text }
    val shouldShowText: Boolean = displayText.isNotBlank()

    val attachmentMetadataLabel: String = listOf(attachmentMimeType, attachmentSizeLabel, attachmentDateLabel)
        .filter { it.isNotBlank() }
        .joinToString(" - ")

    val isAttachmentSet: Boolean = attachmentUri != null ||
            attachmentMimeType.isNotBlank() ||
            attachmentDisplayName.isNotBlank() ||
            attachmentSizeLabel.isNotBlank() ||
            attachmentDateLabel.isNotBlank() ||
            attachmentActionLabel.isNotBlank()

    val openActionLabel: String = attachmentActionLabel
    val shouldShowAttachmentMetadata: Boolean = attachmentMetadataLabel.isNotBlank()
    val shouldShowAttachmentActions: Boolean = isAttachmentSet

    val isPrimaryImageDrawableSet: Boolean = primaryImage != null
    val isPrimaryImageUriSet: Boolean = primaryImageUri != null

    val isSecondaryImageDrawableSet: Boolean = secondaryImage != null
    val isSecondaryImageUriSet: Boolean = secondaryImageUri != null
}
