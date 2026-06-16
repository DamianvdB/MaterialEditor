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
import android.widget.ImageView
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dvdb.materialchecklist.manager.image.model.ImageItem
import com.dvdb.materialchecklist.manager.model.ImageItemContainer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MaterialChecklistImageLoadingInstrumentedTest : MaterialChecklistInstrumentedTestSupport() {

    @Test
    fun imageItemLoadsPrimaryImageUriDrawable() {
        scenarioRule.scenario.onActivity { activity ->
            activity.checklist.setEditorItems(
                listOf(
                    ImageItemContainer(
                        id = 1,
                        items = listOf(
                            ImageItem(
                                id = 22,
                                primaryImageUri = activity.drawableResourceUri(R.drawable.ic_add)
                            )
                        )
                    )
                )
            )
        }
        waitForIdle()

        scenarioRule.scenario.onActivity { activity ->
            val primaryImage = activity.requireViewById<ImageView>(R.id.item_image_primary_image)
            assertEquals(View.VISIBLE, primaryImage.visibility)
            assertTrue(primaryImage.drawable != null)
        }
    }

    @Test
    fun imageItemLoadsSecondaryImageUriDrawable() {
        scenarioRule.scenario.onActivity { activity ->
            activity.checklist.setEditorItems(
                listOf(
                    ImageItemContainer(
                        id = 1,
                        items = listOf(
                            ImageItem(
                                id = 23,
                                secondaryImageUri = activity.drawableResourceUri(R.drawable.ic_close)
                            )
                        )
                    )
                )
            )
        }
        waitForIdle()

        scenarioRule.scenario.onActivity { activity ->
            val secondaryImage = activity.requireViewById<ImageView>(R.id.item_image_secondary_image)
            assertEquals(View.VISIBLE, secondaryImage.visibility)
            assertTrue(secondaryImage.drawable != null)
        }
    }
}
