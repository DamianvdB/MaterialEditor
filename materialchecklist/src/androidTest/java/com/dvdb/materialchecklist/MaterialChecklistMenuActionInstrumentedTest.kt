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

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dvdb.materialchecklist.manager.checklist.model.ChecklistMenuAction
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MaterialChecklistMenuActionInstrumentedTest : MaterialChecklistInstrumentedTestSupport() {

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
}
