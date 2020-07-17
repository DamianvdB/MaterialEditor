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

package com.dvdb.materialchecklist.manager.chip

import com.dvdb.materialchecklist.manager.chip.model.ChipItem
import com.dvdb.materialchecklist.manager.chip.model.ChipManagerConfig
import com.dvdb.materialchecklist.recycler.adapter.ChecklistItemAdapter
import com.dvdb.materialchecklist.recycler.adapter.listener.ChecklistItemAdapterDragListener

internal interface ChipManager : ChecklistItemAdapterDragListener {

    var onItemClicked: (item: ChipItem) -> Unit

    var onItemInContainerClicked: (id: Int) -> Unit

    fun lateInitState(
        adapter: ChecklistItemAdapter,
        config: ChipManagerConfig
    )

    fun setConfig(config: ChipManagerConfig)

    fun getChipItems(): List<ChipItem>

    fun setChipItems(items: List<ChipItem>)

    fun removeChipItems(): Boolean
}