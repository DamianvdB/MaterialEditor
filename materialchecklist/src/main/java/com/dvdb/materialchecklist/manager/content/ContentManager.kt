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

package com.dvdb.materialchecklist.manager.content

import ContentRecyclerHolderItemListener
import com.dvdb.materialchecklist.manager.content.model.ContentManagerConfig
import com.dvdb.materialchecklist.recycler.adapter.ChecklistItemAdapter
import com.dvdb.materialchecklist.recycler.adapter.listener.ChecklistItemAdapterDragListener

internal interface ContentManager :
    ContentRecyclerHolderItemListener,
    ChecklistItemAdapterDragListener {

    fun lateInitState(
        adapter: ChecklistItemAdapter,
        config: ContentManagerConfig
    )

    fun setConfig(config: ContentManagerConfig)

    fun getContentItem(): String?

    fun setContentItem(text: String)

    fun removeContentItem(): Boolean

    fun requestContentItemFocus(): Boolean
}