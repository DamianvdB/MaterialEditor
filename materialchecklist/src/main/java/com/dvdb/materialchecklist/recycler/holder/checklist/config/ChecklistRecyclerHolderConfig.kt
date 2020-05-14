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

package com.dvdb.materialchecklist.recycler.holder.checklist.config

import android.graphics.Typeface
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.Px
import com.dvdb.materialchecklist.config.DragAndDropToggleMode
import com.dvdb.materialchecklist.recycler.holder.base.BaseRecyclerHolderConfig

internal data class ChecklistRecyclerHolderConfig(
    @ColorInt val textColor: Int,
    @Px val textSize: Float,
    val textAlphaCheckedItem: Float,
    val textTypeFace: Typeface?,
    @ColorInt val iconTintColor: Int,
    val iconDragIndicator: Drawable?,
    val iconAlphaDragIndicator: Float,
    val iconDelete: Drawable?,
    val iconAlphaDelete: Float,
    val checkboxAlphaCheckedItem: Float,
    @ColorInt val checkboxTintColor: Int?,
    val dragAndDropToggleMode: DragAndDropToggleMode,
    @ColorInt val dragAndDropActiveBackgroundColor: Int?,
    @Px val topAndBottomPadding: Float,
    @Px val leftAndRightPadding: Float?
) : BaseRecyclerHolderConfig