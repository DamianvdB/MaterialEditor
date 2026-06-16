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

import android.view.View
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MaterialChecklistInsertionInstrumentedTest : MaterialChecklistInstrumentedTestSupport() {

    @Test
    fun addRowCreatesUncheckedItemAtUncheckedCheckedBoundary() {
        scenarioRule.scenario.onActivity { activity ->
            activity.checklist.setItems("[ ] Alpha\n[x] Done")
        }
        waitForIdle()

        scenarioRule.scenario.onActivity { activity ->
            activity.requireViewById<View>(R.id.item_checklist_new_text)
                .parent
                .let { it as View }
                .performClick()
        }
        waitForIdle()

        scenarioRule.scenario.onActivity { activity ->
            val insertedItem = activity.checklist.getChecklistItemAtPosition(1)
            val checkedItem = activity.checklist.getChecklistItemAtPosition(2)

            assertEquals("", insertedItem?.text)
            assertFalse(insertedItem?.isChecked ?: true)
            assertEquals("Done", checkedItem?.text)
            assertEquals("[ ] Alpha\n[ ] \n[x] Done", activity.checklist.getItems())
        }
    }
}
