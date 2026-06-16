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

package com.dvdb.materialchecklist

import android.widget.TextView
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dvdb.materialchecklist.manager.image.model.ImageItem
import com.dvdb.materialchecklist.manager.model.ImageItemContainer
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MaterialChecklistAttachmentInstrumentedTest : MaterialChecklistInstrumentedTestSupport() {

    @Test
    fun attachmentActionButtonsInvokeAttachmentCallbacks() {
        val expectedItem = ImageItem(
            id = 12,
            text = "Quarterly report",
            attachmentDisplayName = "Quarterly report.pdf",
            attachmentMimeType = "application/pdf"
        )
        var openedItem: ImageItem? = null
        var sharedItem: ImageItem? = null
        var removedItem: ImageItem? = null

        scenarioRule.scenario.onActivity { activity ->
            activity.checklist.setOnAttachmentItemOpenClicked { openedItem = it }
            activity.checklist.setOnAttachmentItemShareClicked { sharedItem = it }
            activity.checklist.setOnAttachmentItemRemoveClicked { removedItem = it }
            activity.checklist.setEditorItems(
                listOf(
                    ImageItemContainer(
                        id = 1,
                        items = listOf(expectedItem)
                    )
                )
            )
        }
        waitForIdle()

        scenarioRule.scenario.onActivity { activity ->
            val openAction = activity.requireViewById<TextView>(R.id.item_image_action_open)
            assertEquals(activity.getString(R.string.mc_item_image_action_open), openAction.text.toString())

            openAction.performClick()
            activity.requireViewById<TextView>(R.id.item_image_action_share).performClick()
            activity.requireViewById<TextView>(R.id.item_image_action_remove).performClick()
        }

        scenarioRule.scenario.onActivity {
            assertEquals(expectedItem, openedItem)
            assertEquals(expectedItem, sharedItem)
            assertEquals(expectedItem, removedItem)
        }
    }
}
