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

package com.dvdb.materialchecklist.manager.image

import com.dvdb.materialchecklist.manager.image.model.ImageItem
import com.dvdb.materialchecklist.recycler.imagecontainer.image.model.ImageRecyclerItem
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class ImageManagerImplTest {

    @Test
    fun attachmentOpen_defaultsToImageClickCallback_test() {
        val manager = ImageManagerImpl()
        var clickedItem: ImageItem? = null
        manager.onImageItemClicked = {
            clickedItem = it
        }

        manager.onAttachmentItemInContainerOpened(imageRecyclerItem())

        Assertions.assertEquals(
            imageItem(),
            clickedItem
        )
    }

    @Test
    fun attachmentShare_invokesShareCallback_test() {
        val manager = ImageManagerImpl()
        var sharedItem: ImageItem? = null
        manager.onAttachmentItemShared = {
            sharedItem = it
        }

        manager.onAttachmentItemInContainerShared(imageRecyclerItem())

        Assertions.assertEquals(
            imageItem(),
            sharedItem
        )
    }

    @Test
    fun attachmentRemove_invokesRemoveCallback_test() {
        val manager = ImageManagerImpl()
        var removedItem: ImageItem? = null
        manager.onAttachmentItemRemoved = {
            removedItem = it
        }

        manager.onAttachmentItemInContainerRemoved(imageRecyclerItem())

        Assertions.assertEquals(
            imageItem(),
            removedItem
        )
    }

    private fun imageItem() = ImageItem(
        id = 1,
        text = "Holiday booking",
        attachmentDisplayName = "Holiday booking.pdf"
    )

    private fun imageRecyclerItem() = ImageRecyclerItem(
        id = 1,
        text = "Holiday booking",
        primaryImage = null,
        secondaryImage = null,
        attachmentDisplayName = "Holiday booking.pdf"
    )
}
