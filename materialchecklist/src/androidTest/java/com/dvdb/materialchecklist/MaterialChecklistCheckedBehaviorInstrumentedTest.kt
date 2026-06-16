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

import android.widget.CheckBox
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dvdb.materialchecklist.config.checklist.model.BehaviorCheckedItem
import com.dvdb.materialchecklist.config.checklist.setOnItemCheckedBehavior
import com.dvdb.materialchecklist.config.general.applyConfiguration
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MaterialChecklistCheckedBehaviorInstrumentedTest : MaterialChecklistInstrumentedTestSupport() {

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
}
