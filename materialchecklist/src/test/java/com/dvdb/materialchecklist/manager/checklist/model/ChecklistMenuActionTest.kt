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

package com.dvdb.materialchecklist.manager.checklist.model

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class ChecklistMenuActionTest {

    @Test
    fun fromString_convertToText_test() {
        val expected = ChecklistMenuAction.CONVERT_TO_TEXT
        val actual = ChecklistMenuAction.fromString("convert_to_text")

        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun fromString_removeCheckedItems_test() {
        val expected = ChecklistMenuAction.REMOVE_CHECKED_ITEMS
        val actual = ChecklistMenuAction.fromString("REMOVE_CHECKED_ITEMS")

        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun fromString_uncheckCheckedItems_test() {
        val expected = ChecklistMenuAction.UNCHECK_CHECKED_ITEMS
        val actual = ChecklistMenuAction.fromString("uncheck_checked_items")

        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun fromString_invalid_test() {
        val actual = ChecklistMenuAction.fromString("")

        Assertions.assertNull(actual)
    }

    @Test
    fun result_defaultsToNoChange_test() {
        val actual = ChecklistMenuActionResult()

        Assertions.assertNull(actual.formattedText)
        Assertions.assertTrue(actual.removedItemIds.isEmpty())
        Assertions.assertFalse(actual.changed)
    }
}
