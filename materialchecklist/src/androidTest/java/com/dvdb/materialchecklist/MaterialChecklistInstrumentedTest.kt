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

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.dvdb.materialchecklist.config.checklist.model.BehaviorCheckedItem
import com.dvdb.materialchecklist.config.checklist.setOnItemCheckedBehavior
import com.dvdb.materialchecklist.config.general.applyConfiguration
import com.dvdb.materialchecklist.manager.checklist.model.ChecklistMenuAction
import com.dvdb.materialchecklist.manager.image.model.ImageItem
import com.dvdb.materialchecklist.manager.model.ImageItemContainer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MaterialChecklistInstrumentedTest {

    @get:Rule
    val scenarioRule = ActivityScenarioRule(MaterialChecklistTestActivity::class.java)

    @Test
    fun removeCheckedItemsMenuActionRemovesCheckedRows() {
        scenarioRule.scenario.onActivity { activity ->
            val checklist = activity.checklist
            checklist.setItems("[ ] Alpha\n[x] Done")

            val result = checklist.performChecklistMenuAction(ChecklistMenuAction.REMOVE_CHECKED_ITEMS)

            assertTrue(result.changed)
            assertEquals(1, result.removedItemIds.size)
            assertTrue(checklist.getItems().contains("Alpha"))
            assertFalse(checklist.getItems().contains("Done"))
        }
    }

    @Test
    fun uncheckCheckedItemsMenuActionClearsCheckedRows() {
        scenarioRule.scenario.onActivity { activity ->
            val checklist = activity.checklist
            checklist.setItems("[ ] Alpha\n[x] Done")

            val result = checklist.performChecklistMenuAction(ChecklistMenuAction.UNCHECK_CHECKED_ITEMS)

            assertTrue(result.changed)
            assertEquals(0, checklist.getCheckedItemCount())
            assertTrue(checklist.getItems().contains("[ ] Done"))
        }
    }

    @Test
    fun convertToTextMenuActionReturnsFormattedChecklistText() {
        scenarioRule.scenario.onActivity { activity ->
            val checklist = activity.checklist
            checklist.setItems("[ ] Alpha\n[x] Done")

            val result = checklist.performChecklistMenuAction(ChecklistMenuAction.CONVERT_TO_TEXT)

            assertEquals("[ ] Alpha\n[x] Done", result.formattedText)
            assertFalse(result.changed)
        }
    }

    @Test
    fun deleteCheckedItemBehaviorRemovesRowWhenCheckboxIsClicked() {
        scenarioRule.scenario.onActivity { activity ->
            activity.checklist
                .setOnItemCheckedBehavior(BehaviorCheckedItem.DELETE)
                .applyConfiguration()
            activity.checklist.setItems("[ ] Delete me\n[ ] Keep me")
        }
        waitForIdle()

        scenarioRule.scenario.onActivity { activity ->
            activity.requireViewById<CheckBox>(R.id.item_checklist_checkbox).performClick()
        }
        waitForIdle()

        scenarioRule.scenario.onActivity { activity ->
            val formattedItems = activity.checklist.getItems()
            assertFalse(formattedItems.contains("Delete me"))
            assertTrue(formattedItems.contains("Keep me"))
        }
    }

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

    private fun waitForIdle() {
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
    }
}

class MaterialChecklistTestActivity : Activity() {
    lateinit var checklist: MaterialChecklist
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checklist = MaterialChecklist(this, null)
        setContentView(
            checklist,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
    }
}

private inline fun <reified T : View> Activity.requireViewById(id: Int): T =
    findViewById<View>(id) as? T ?: error("Required view with id $id was not found")
