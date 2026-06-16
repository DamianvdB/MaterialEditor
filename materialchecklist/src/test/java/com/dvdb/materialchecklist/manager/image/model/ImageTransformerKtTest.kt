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

package com.dvdb.materialchecklist.manager.image.model

import com.dvdb.materialchecklist.manager.model.ImageItemContainer
import com.dvdb.materialchecklist.recycler.imagecontainer.image.model.ImageRecyclerItem
import com.dvdb.materialchecklist.recycler.imagecontainer.model.ImageContainerRecyclerItem
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class ImageTransformerKtTest {

    @Test
    fun transformToImageContainerRecyclerItem() {
        val expectedImageContainerRecyclerItem = ImageContainerRecyclerItem(
            1,
            items = listOf(imageRecyclerItem())
        )

        val actualImageContainerRecyclerItem: ImageContainerRecyclerItem = ImageItemContainer(
            id = 1,
            items = expectedImageContainerRecyclerItem.items.map { it.transform() }
        ).transform()

        Assertions.assertEquals(
            expectedImageContainerRecyclerItem.items,
            actualImageContainerRecyclerItem.items
        )
    }

    @Test
    fun transformToImageItemContainer() {
        val expectedImageItemContainer = ImageItemContainer(
            1,
            items = listOf(imageItem())
        )

        val actualImageItemContainer: ImageItemContainer = ImageContainerRecyclerItem(
            id = expectedImageItemContainer.id.toLong(),
            items = expectedImageItemContainer.items.map { it.transform() }
        ).transform()

        Assertions.assertEquals(
            expectedImageItemContainer,
            actualImageItemContainer
        )
    }

    @Test
    fun transformToImageRecyclerItemWithAttachmentMetadata() {
        val expectedImageRecyclerItem = imageRecyclerItem()

        val actualImageRecyclerItem: ImageRecyclerItem = imageItem().transform()

        Assertions.assertEquals(
            expectedImageRecyclerItem,
            actualImageRecyclerItem
        )
    }

    @Test
    fun transformToImageItemWithAttachmentMetadata() {
        val expectedImageItem = imageItem()

        val actualImageItem: ImageItem = imageRecyclerItem().transform()

        Assertions.assertEquals(
            expectedImageItem,
            actualImageItem
        )
    }

    private fun imageItem() = ImageItem(
        id = 1,
        text = "Holiday booking",
        attachmentDisplayName = "Holiday booking.pdf",
        attachmentMimeType = "application/pdf",
        attachmentSizeLabel = "128 KB",
        attachmentDateLabel = "Today",
        attachmentActionLabel = "Open"
    )

    private fun imageRecyclerItem() = ImageRecyclerItem(
        id = 1,
        text = "Holiday booking",
        primaryImage = null,
        secondaryImage = null,
        attachmentDisplayName = "Holiday booking.pdf",
        attachmentMimeType = "application/pdf",
        attachmentSizeLabel = "128 KB",
        attachmentDateLabel = "Today",
        attachmentActionLabel = "Open"
    )
}
