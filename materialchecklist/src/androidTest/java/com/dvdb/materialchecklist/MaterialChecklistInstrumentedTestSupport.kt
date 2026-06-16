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
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule

abstract class MaterialChecklistInstrumentedTestSupport {

    @get:Rule
    val scenarioRule = ActivityScenarioRule(MaterialChecklistTestActivity::class.java)

    protected fun waitForIdle() {
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

internal fun Activity.drawableResourceUri(drawableId: Int): Uri =
    Uri.parse("android.resource://$packageName/$drawableId")

internal inline fun <reified T : View> Activity.requireViewById(id: Int): T =
    findViewById<View>(id) as? T ?: error("Required view with id $id was not found")
