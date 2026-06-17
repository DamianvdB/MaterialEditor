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

import android.view.Menu
import android.view.MenuItem
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dvdb.materialchecklist.config.checklist.getOnItemCheckedBehavior
import com.dvdb.materialchecklist.config.checklist.model.BehaviorCheckedItem
import com.dvdb.materialchecklist.manager.checklist.model.ChecklistMenuAction
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MaterialChecklistPopupMenuInstrumentedTest : MaterialChecklistInstrumentedTestSupport() {

    @Test
    fun checklistMenuDispatchesExistingActions() {
        var selectedAction: ChecklistMenuAction? = null

        scenarioRule.scenario.onActivity { activity ->
            val checklist = activity.checklist
            checklist.setItems("[ ] Alpha\n[x] Done")

            val popupMenu = checklist.createChecklistMenu(
                anchor = checklist,
                onActionResult = { action, _ -> selectedAction = action }
            )

            popupMenu.menu.performAction(activity.getString(R.string.mc_checklist_menu_remove_checked))

            assertEquals(ChecklistMenuAction.REMOVE_CHECKED_ITEMS, selectedAction)
            assertTrue(checklist.getItems().contains("Alpha"))
            assertFalse(checklist.getItems().contains("Done"))
        }
    }

    @Test
    fun checklistMenuCanHideDeleteItemWhenChecked() {
        scenarioRule.scenario.onActivity { activity ->
            val checklist = activity.checklist

            val popupMenu = checklist.createChecklistMenu(
                anchor = checklist,
                showDeleteItemWhenChecked = false
            )

            assertNull(
                popupMenu.menu.findItemWithTitle(
                    activity.getString(R.string.mc_checklist_menu_delete_item_when_checked)
                )
            )
        }
    }

    @Test
    fun checklistMenuTogglesDeleteItemWhenCheckedByDefault() {
        scenarioRule.scenario.onActivity { activity ->
            val checklist = activity.checklist

            val popupMenu = checklist.createChecklistMenu(anchor = checklist)

            popupMenu.menu.performAction(
                activity.getString(R.string.mc_checklist_menu_delete_item_when_checked)
            )

            assertEquals(BehaviorCheckedItem.DELETE, checklist.getOnItemCheckedBehavior())
        }
    }

    @Test
    fun checklistMenuCanDelegateDeleteItemWhenCheckedToggle() {
        var deleteItemWhenChecked: Boolean? = null

        scenarioRule.scenario.onActivity { activity ->
            val checklist = activity.checklist

            val popupMenu = checklist.createChecklistMenu(
                anchor = checklist,
                deleteItemWhenChecked = false,
                onDeleteItemWhenCheckedChanged = { deleteItemWhenChecked = it }
            )

            val item =
                popupMenu.menu.findRequiredItemWithTitle(
                    activity.getString(R.string.mc_checklist_menu_delete_item_when_checked)
                )
            assertTrue(item.isCheckable)
            assertFalse(item.isChecked)

            popupMenu.menu.performAction(item.title.toString())

            assertEquals(true, deleteItemWhenChecked)
            assertEquals(
                BehaviorCheckedItem.MOVE_TO_TOP_OF_CHECKED_ITEMS,
                checklist.getOnItemCheckedBehavior()
            )
        }
    }
}

private fun Menu.performAction(title: String) {
    val item = findRequiredItemWithTitle(title)
    performIdentifierAction(item.itemId, 0)
}

private fun Menu.findRequiredItemWithTitle(title: String): MenuItem {
    return findItemWithTitle(title) ?: error("Required menu item $title was not found")
}

private fun Menu.findItemWithTitle(title: String): MenuItem? {
    for (index in 0 until size()) {
        val item = getItem(index)
        if (item.title == title) {
            return item
        }
    }
    return null
}
