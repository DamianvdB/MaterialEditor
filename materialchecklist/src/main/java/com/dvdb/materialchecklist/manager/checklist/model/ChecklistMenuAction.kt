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

/**
 * User-facing checklist overflow actions used by D Notes-style editor menus.
 */
enum class ChecklistMenuAction {
    CONVERT_TO_TEXT,
    REMOVE_CHECKED_ITEMS,
    UNCHECK_CHECKED_ITEMS;

    companion object {
        fun fromString(value: String): ChecklistMenuAction? {
            return values().firstOrNull {
                it.name.equals(value, ignoreCase = true)
            }
        }
    }
}

data class ChecklistMenuActionResult(
    val formattedText: String? = null,
    val removedItemIds: List<Long> = emptyList(),
    val changed: Boolean = false
)
