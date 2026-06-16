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

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class ImageRecyclerItemTest {

    @Test
    fun displayText_usesAttachmentDisplayNameWhenSet_test() {
        val item = imageRecyclerItem(
            text = "fallback",
            attachmentDisplayName = "attachment.pdf"
        )

        Assertions.assertEquals(
            "attachment.pdf",
            item.displayText
        )
    }

    @Test
    fun attachmentMetadataLabel_joinsAvailableMetadata_test() {
        val item = imageRecyclerItem(
            attachmentMimeType = "application/pdf",
            attachmentSizeLabel = "128 KB",
            attachmentDateLabel = "Today"
        )

        Assertions.assertEquals(
            "application/pdf - 128 KB - Today",
            item.attachmentMetadataLabel
        )
    }

    @Test
    fun shouldShowAttachmentActions_whenAttachmentMetadataExists_test() {
        val item = imageRecyclerItem(attachmentDisplayName = "attachment.pdf")

        Assertions.assertTrue(item.shouldShowAttachmentActions)
    }

    @Test
    fun openActionLabel_defaultsToEmpty_test() {
        val item = imageRecyclerItem(attachmentDisplayName = "attachment.pdf")

        Assertions.assertEquals(
            "",
            item.openActionLabel
        )
    }

    @Test
    fun openActionLabel_usesConfiguredLabel_test() {
        val item = imageRecyclerItem(
            attachmentDisplayName = "attachment.pdf",
            attachmentActionLabel = "Preview"
        )

        Assertions.assertEquals(
            "Preview",
            item.openActionLabel
        )
    }

    @Test
    fun blankItem_hidesTextAndAttachmentActions_test() {
        val item = imageRecyclerItem()

        Assertions.assertFalse(item.shouldShowText)
        Assertions.assertFalse(item.shouldShowAttachmentActions)
        Assertions.assertFalse(item.shouldShowAttachmentMetadata)
    }

    @Test
    fun attachmentActionLabelShowsActionsWithoutMetadataText_test() {
        val item = imageRecyclerItem(attachmentActionLabel = "Preview")

        Assertions.assertTrue(item.shouldShowAttachmentActions)
        Assertions.assertFalse(item.shouldShowAttachmentMetadata)
    }

    private fun imageRecyclerItem(
        text: String = "",
        attachmentMimeType: String = "",
        attachmentDisplayName: String = "",
        attachmentSizeLabel: String = "",
        attachmentDateLabel: String = "",
        attachmentActionLabel: String = ""
    ) = ImageRecyclerItem(
        id = 1,
        text = text,
        primaryImage = null,
        secondaryImage = null,
        attachmentMimeType = attachmentMimeType,
        attachmentDisplayName = attachmentDisplayName,
        attachmentSizeLabel = attachmentSizeLabel,
        attachmentDateLabel = attachmentDateLabel,
        attachmentActionLabel = attachmentActionLabel
    )
}
