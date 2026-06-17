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

import android.content.Context
import android.util.AttributeSet
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.CheckResult
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dvdb.materialchecklist.config.ChecklistConfig
import com.dvdb.materialchecklist.config.checklist.model.BehaviorCheckedItem
import com.dvdb.materialchecklist.config.checklist.setOnItemCheckedBehavior
import com.dvdb.materialchecklist.config.general.applyConfiguration
import com.dvdb.materialchecklist.manager.Manager
import com.dvdb.materialchecklist.manager.checklist.ChecklistManagerImpl
import com.dvdb.materialchecklist.manager.checklist.model.ChecklistItem
import com.dvdb.materialchecklist.manager.checklist.model.ChecklistMenuAction
import com.dvdb.materialchecklist.manager.checklist.model.ChecklistMenuActionResult
import com.dvdb.materialchecklist.manager.checklist.util.ChecklistRecyclerItemPositionTracker
import com.dvdb.materialchecklist.manager.chip.ChipManagerImpl
import com.dvdb.materialchecklist.manager.chip.model.ChipItem
import com.dvdb.materialchecklist.manager.content.ContentManagerImpl
import com.dvdb.materialchecklist.manager.image.ImageManagerImpl
import com.dvdb.materialchecklist.manager.image.model.ImageItem
import com.dvdb.materialchecklist.manager.model.BaseItem
import com.dvdb.materialchecklist.manager.model.ChecklistItemContainer
import com.dvdb.materialchecklist.manager.model.ChipItemContainer
import com.dvdb.materialchecklist.manager.model.TitleItem
import com.dvdb.materialchecklist.manager.title.TitleManagerImpl
import com.dvdb.materialchecklist.recycler.adapter.ChecklistItemAdapter
import com.dvdb.materialchecklist.recycler.base.model.BaseRecyclerItem
import com.dvdb.materialchecklist.recycler.checklist.holder.ChecklistRecyclerHolder
import com.dvdb.materialchecklist.recycler.checklistnew.holder.ChecklistNewRecyclerHolder
import com.dvdb.materialchecklist.recycler.chipcontainer.holder.ChipContainerRecyclerHolder
import com.dvdb.materialchecklist.recycler.content.holder.ContentRecyclerHolder
import com.dvdb.materialchecklist.recycler.imagecontainer.holder.ImageContainerRecyclerHolder
import com.dvdb.materialchecklist.recycler.title.holder.TitleRecyclerHolder
import com.dvdb.materialchecklist.recycler.util.ItemTouchHelperAdapter
import com.dvdb.materialchecklist.recycler.util.RecyclerSpaceItemDecorator
import com.dvdb.materialchecklist.recycler.util.SimpleItemTouchHelper
import com.dvdb.materialchecklist.recycler.util.holder.EnterActionPerformedFactory
import com.dvdb.materialchecklist.util.hideKeyboard
import com.dvdb.materialchecklist.util.updateLayoutParams

class MaterialChecklist(
    context: Context,
    attrs: AttributeSet?
) : FrameLayout(context, attrs) {

    internal val manager = Manager(
        TitleManagerImpl(),
        ContentManagerImpl(),
        ChecklistManagerImpl(
            itemPositionTracker = ChecklistRecyclerItemPositionTracker { items },
            hideKeyboard = {
                hideKeyboard()
                requestFocus()
            }
        ),
        ChipManagerImpl(),
        ImageManagerImpl()
    )

    internal val config: ChecklistConfig = ChecklistConfig(
        context = context,
        attrs = attrs
    )

    private val recyclerView: RecyclerView

    @Suppress("UNNECESSARY_SAFE_CALL")
    private val items: List<BaseRecyclerItem>
        get() = (recyclerView?.adapter as? ChecklistItemAdapter)?.items ?: emptyList()

    init {
        addFocusableView()

        recyclerView = createRecyclerView()
        addView(recyclerView)

        val itemTouchCallback =
            SimpleItemTouchHelper(recyclerView.adapter as ItemTouchHelperAdapter)
        val itemTouchHelper = ItemTouchHelper(itemTouchCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
        initManager(recyclerView, itemTouchHelper, itemTouchCallback)

        initDefaultChecklistItems()
    }

    fun setEditorItems(items: List<BaseItem>) {
        manager.setItems(items)
    }

    fun getEditorItems(
        keepCheckboxSymbolsOfChecklistItems: Boolean = true,
        keepCheckedItems: Boolean = true
    ): List<BaseItem> {
        return manager.getItems(
            keepCheckboxSymbolsOfChecklistItems,
            keepCheckedItems
        )
    }

    fun getEditorItemWithFocus(): BaseItem? {
        return manager.getItemWithFocus()
    }

    /**
     * Title item
     */
    fun setOnTitleItemActionIconClicked(onActionIconClicked: () -> Unit) {
        manager.onTitleItemActionIconClicked = onActionIconClicked
    }

    /**
     * Chip item
     */
    fun setOnChipItemClicked(onChipItemClicked: (ChipItem) -> Unit) {
        manager.onChipItemClicked = onChipItemClicked
    }

    fun setOnChipItemLongClicked(onChipItemLongClicked: (ChipItem) -> Boolean) {
        manager.onChipItemLongClicked = onChipItemLongClicked
    }

    /**
     * Image item
     */
    fun setOnImageItemClicked(onImageItemClicked: (ImageItem) -> Unit) {
        manager.onImageItemClicked = onImageItemClicked
    }

    fun setOnImageItemLongClicked(onImageItemLongClicked: (ImageItem) -> Boolean) {
        manager.onImageItemLongClicked = onImageItemLongClicked
    }

    fun setOnAttachmentItemClicked(onAttachmentItemClicked: (ImageItem) -> Unit) {
        setOnAttachmentItemOpenClicked(onAttachmentItemClicked)
    }

    fun setOnAttachmentItemLongClicked(onAttachmentItemLongClicked: (ImageItem) -> Boolean) {
        setOnImageItemLongClicked(onAttachmentItemLongClicked)
    }

    fun setOnAttachmentItemOpenClicked(onAttachmentItemOpenClicked: (ImageItem) -> Unit) {
        manager.onAttachmentItemOpened = onAttachmentItemOpenClicked
    }

    fun setOnAttachmentItemShareClicked(onAttachmentItemShareClicked: (ImageItem) -> Unit) {
        manager.onAttachmentItemShared = onAttachmentItemShareClicked
    }

    fun setOnAttachmentItemRemoveClicked(onAttachmentItemRemoveClicked: (ImageItem) -> Unit) {
        manager.onAttachmentItemRemoved = onAttachmentItemRemoveClicked
    }

    /**
     * Set the list of checklist items by parsing the [formattedText] string.
     *
     * @param formattedText The formatted string to parse containing the checklist items.
     */
    fun setItems(formattedText: String) {
        manager.setItems(formattedText)
    }

    /**
     * Get the formatted string representation of the checklist items.
     *
     * Be careful with editing the result of this method. Edits to the returned string may result in
     * the loss of state to the checklist items.
     *
     * @param keepCheckboxSymbols The flag to keep or remove the checkbox symbols of the checklist items.
     * @param keepCheckedItems The flag to keep or remove the checklist items that are marked as checked.
     * @return The formatted string representation of the checklist items.
     */
    @CheckResult
    fun getItems(
        keepCheckboxSymbols: Boolean = true,
        keepCheckedItems: Boolean = true
    ): String {
        return manager.getFormattedTextItems(
            keepCheckboxSymbols,
            keepCheckedItems
        )
    }

    /**
     * Set a listener for when a checklist item is deleted.
     *
     * @param listener The listener to be notified when a checklist item is deleted.
     */
    fun setOnItemDeletedListener(listener: ((text: String, itemId: Long) -> Unit)) {
        manager.onItemDeleted = listener
    }

    /**
     * Restore deleted checklist items.
     *
     * @param itemIds The id's of the checklist items to restore.
     * @return 'true' if all items were restored, otherwise 'false'.
     */
    fun restoreDeletedItems(itemIds: List<Long>): Boolean {
        return manager.restoreDeletedItems(itemIds)
    }

    /**
     * Restore a deleted checklist item.
     *
     * @param itemId The id of the checklist item to restore.
     * @return 'true' if the item was restored, otherwise 'false'.
     */
    fun restoreDeletedItem(itemId: Long): Boolean {
        return manager.restoreDeletedItem(itemId)
    }

    /**
     * Remove all the checklist items that are marked as checked.
     * These items can be restored using their id's.
     *
     * @return id's of the checklist items removed.
     */
    fun removeAllCheckedItems(): List<Long> {
        return manager.removeAllCheckedItems()
    }

    /**
     * Uncheck all the checklist items that are marked as checked.
     *
     * @return 'true' if any checked items were marked as unchecked, otherwise 'false'.
     */
    fun uncheckAllCheckedItems(): Boolean {
        return manager.uncheckAllCheckedItems()
    }

    /**
     * Perform a D Notes-style checklist overflow action.
     *
     * Conversion to checklist is intentionally kept by [setItems] because the caller owns the
     * plain-text source. Conversion to text returns the formatted text to render in the caller's
     * plain editor surface.
     */
    @CheckResult
    fun performChecklistMenuAction(
        action: ChecklistMenuAction,
        keepCheckboxSymbols: Boolean = true,
        keepCheckedItems: Boolean = true
    ): ChecklistMenuActionResult {
        return when (action) {
            ChecklistMenuAction.CONVERT_TO_TEXT -> ChecklistMenuActionResult(
                formattedText = getItems(
                    keepCheckboxSymbols = keepCheckboxSymbols,
                    keepCheckedItems = keepCheckedItems
                )
            )
            ChecklistMenuAction.REMOVE_CHECKED_ITEMS -> {
                val removedIds = removeAllCheckedItems()
                ChecklistMenuActionResult(
                    removedItemIds = removedIds,
                    changed = removedIds.isNotEmpty()
                )
            }
            ChecklistMenuAction.UNCHECK_CHECKED_ITEMS -> ChecklistMenuActionResult(changed = uncheckAllCheckedItems())
        }
    }

    /**
     * Create a reusable D Notes-style checklist overflow menu for this editor.
     *
     * The returned [PopupMenu] is useful when callers need to add more host-owned actions before
     * showing it. Use [showChecklistMenu] when no additional configuration is needed.
     */
    @CheckResult
    fun createChecklistMenu(
        anchor: View,
        keepCheckboxSymbols: Boolean = true,
        keepCheckedItems: Boolean = true,
        showDeleteItemWhenChecked: Boolean = true,
        deleteItemWhenChecked: Boolean = config.behaviorCheckedItem == BehaviorCheckedItem.DELETE,
        onDeleteItemWhenCheckedChanged: ((Boolean) -> Unit)? = null,
        onActionResult: (ChecklistMenuAction, ChecklistMenuActionResult) -> Unit = { _, _ -> }
    ): PopupMenu {
        return PopupMenu(anchor.context, anchor).apply {
            menu.addChecklistAction(
                itemId = CHECKLIST_MENU_ITEM_CONVERT_TO_TEXT,
                order = 0,
                title = context.getString(R.string.mc_checklist_menu_convert_to_text)
            )
            menu.addChecklistAction(
                itemId = CHECKLIST_MENU_ITEM_UNCHECK_ALL,
                order = 1,
                title = context.getString(R.string.mc_checklist_menu_uncheck_all)
            )
            menu.addChecklistAction(
                itemId = CHECKLIST_MENU_ITEM_REMOVE_CHECKED,
                order = 2,
                title = context.getString(R.string.mc_checklist_menu_remove_checked)
            )
            if (showDeleteItemWhenChecked) {
                menu.addChecklistAction(
                    itemId = CHECKLIST_MENU_ITEM_DELETE_ITEM_WHEN_CHECKED,
                    order = 3,
                    title = context.getString(R.string.mc_checklist_menu_delete_item_when_checked)
                ).apply {
                    isCheckable = true
                    isChecked = deleteItemWhenChecked
                }
            }
            setOnMenuItemClickListener { item ->
                handleChecklistMenuItemClick(
                    item = item,
                    keepCheckboxSymbols = keepCheckboxSymbols,
                    keepCheckedItems = keepCheckedItems,
                    onDeleteItemWhenCheckedChanged = onDeleteItemWhenCheckedChanged,
                    onActionResult = onActionResult
                )
            }
        }
    }

    /**
     * Show a reusable D Notes-style checklist overflow menu for this editor.
     */
    fun showChecklistMenu(
        anchor: View,
        keepCheckboxSymbols: Boolean = true,
        keepCheckedItems: Boolean = true,
        showDeleteItemWhenChecked: Boolean = true,
        deleteItemWhenChecked: Boolean = config.behaviorCheckedItem == BehaviorCheckedItem.DELETE,
        onDeleteItemWhenCheckedChanged: ((Boolean) -> Unit)? = null,
        onActionResult: (ChecklistMenuAction, ChecklistMenuActionResult) -> Unit = { _, _ -> }
    ): PopupMenu {
        return createChecklistMenu(
            anchor = anchor,
            keepCheckboxSymbols = keepCheckboxSymbols,
            keepCheckedItems = keepCheckedItems,
            showDeleteItemWhenChecked = showDeleteItemWhenChecked,
            deleteItemWhenChecked = deleteItemWhenChecked,
            onDeleteItemWhenCheckedChanged = onDeleteItemWhenCheckedChanged,
            onActionResult = onActionResult
        ).also { it.show() }
    }

    private fun handleChecklistMenuItemClick(
        item: MenuItem,
        keepCheckboxSymbols: Boolean,
        keepCheckedItems: Boolean,
        onDeleteItemWhenCheckedChanged: ((Boolean) -> Unit)?,
        onActionResult: (ChecklistMenuAction, ChecklistMenuActionResult) -> Unit
    ): Boolean {
        return when (item.itemId) {
            CHECKLIST_MENU_ITEM_CONVERT_TO_TEXT -> {
                performChecklistMenuAction(
                    action = ChecklistMenuAction.CONVERT_TO_TEXT,
                    keepCheckboxSymbols = keepCheckboxSymbols,
                    keepCheckedItems = keepCheckedItems
                ).also { result ->
                    onActionResult(ChecklistMenuAction.CONVERT_TO_TEXT, result)
                }
                true
            }
            CHECKLIST_MENU_ITEM_UNCHECK_ALL -> {
                performChecklistMenuAction(ChecklistMenuAction.UNCHECK_CHECKED_ITEMS)
                    .also { result ->
                        onActionResult(ChecklistMenuAction.UNCHECK_CHECKED_ITEMS, result)
                    }
                true
            }
            CHECKLIST_MENU_ITEM_REMOVE_CHECKED -> {
                performChecklistMenuAction(ChecklistMenuAction.REMOVE_CHECKED_ITEMS)
                    .also { result ->
                        onActionResult(ChecklistMenuAction.REMOVE_CHECKED_ITEMS, result)
                    }
                true
            }
            CHECKLIST_MENU_ITEM_DELETE_ITEM_WHEN_CHECKED -> {
                val enabled = !item.isChecked
                item.isChecked = enabled
                if (onDeleteItemWhenCheckedChanged != null) {
                    onDeleteItemWhenCheckedChanged(enabled)
                } else {
                    setOnItemCheckedBehavior(
                        if (enabled) {
                            BehaviorCheckedItem.DELETE
                        } else {
                            BehaviorCheckedItem.MOVE_TO_TOP_OF_CHECKED_ITEMS
                        }
                    ).applyConfiguration()
                }
                true
            }
            else -> false
        }
    }

    /**
     * Get the total number of checklist items.
     *
     * @return number of checklist items.
     */
    @CheckResult
    fun getItemCount(): Int {
        return manager.getItemCount()
    }

    /**
     * Get the total number of checklist items that are marked as checked.
     *
     * @return number of checklist items marked as checked.
     */
    @CheckResult
    fun getCheckedItemCount(): Int {
        return manager.getCheckedItemCount()
    }

    /**
     * Get the position of the checklist item in the list that has focus.
     *
     * @return checklist item focus position, otherwise -1 if no item has focus.
     */
    @CheckResult
    fun getItemFocusPosition(): Int {
        return manager.getItemFocusPosition()
    }

    /**
     * Set the focus on the checklist item at [position] in the list,
     * with the selection at the end of the item's text.
     *
     * @return 'true' if focus could be set on a checklist item, otherwise 'false.
     */
    fun setItemFocusPosition(position: Int): Boolean {
        return manager.setItemFocusPosition(position)
    }

    /**
     * Get the checklist item at [position] in the list.
     *
     * @return checklist item at [position] or null if no item could be found
     * at [position] in the list.
     */
    @CheckResult
    fun getChecklistItemAtPosition(position: Int): ChecklistItem? {
        return manager.getChecklistItemAtPosition(position)
    }

    /**
     * Update the checklist [item] in the list with same id.
     *
     * @return 'true' if a checklist item with the same id could be found
     * and it has different values when compared to [item]. Otherwise, return 'false'
     * if a checklist item could not be found or they have same values.
     */
    fun updateChecklistItem(item: ChecklistItem): Boolean {
        return manager.updateChecklistItem(item)
    }

    private fun Menu.addChecklistAction(
        itemId: Int,
        order: Int,
        title: String
    ): MenuItem {
        return add(0, itemId, order, title)
    }

    private fun addFocusableView() {
        val focusableView = View(context)
        focusableView.isFocusableInTouchMode = true

        addView(focusableView)

        focusableView.updateLayoutParams {
            height = 1
        }
    }

    private fun createRecyclerView(): RecyclerView {
        val recyclerView = RecyclerView(context)

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.overScrollMode = View.OVER_SCROLL_NEVER
        val enterActionPerformedFactory = EnterActionPerformedFactory()
        recyclerView.adapter = ChecklistItemAdapter(
            config = config.toAdapterConfig(),
            itemTitleRecyclerHolderFactory = TitleRecyclerHolder.Factory(
                manager
            ),
            itemContentRecyclerHolderFactory = ContentRecyclerHolder.Factory(
                manager
            ),
            itemRecyclerHolderFactory = ChecklistRecyclerHolder.Factory(
                enterActionPerformedFactory,
                manager
            ),
            itemNewRecyclerHolderFactory = ChecklistNewRecyclerHolder.Factory(
                manager.onCreateNewChecklistItemClicked
            ),
            itemChipContainerRecyclerHolderFactory = ChipContainerRecyclerHolder.Factory(
                onItemClicked = { item ->
                    manager.onChipItemInContainerClicked(item)
                },
                onItemLongClicked = { item ->
                    manager.onChipItemInContainerLongClicked(item)
                }
            ),
            itemImageContainerRecyclerHolderFactory = ImageContainerRecyclerHolder.Factory(
                onItemOpened = { item ->
                    manager.onAttachmentItemInContainerOpened(item)
                },
                onItemShared = { item ->
                    manager.onAttachmentItemInContainerShared(item)
                },
                onItemRemoved = { item ->
                    manager.onAttachmentItemInContainerRemoved(item)
                },
                onItemLongClicked = { item ->
                    manager.onImageItemInContainerLongClicked(item)
                }
            ),
            itemDragListener = manager
        )

        return recyclerView
    }

    private fun initManager(
        recyclerView: RecyclerView,
        itemTouchHelper: ItemTouchHelper,
        itemTouchCallback: SimpleItemTouchHelper
    ) {
        val adapter = recyclerView.adapter as ChecklistItemAdapter

        manager.lateInitState(
            adapter = adapter,
            config = config,
            scrollToPosition = createManagerScrollToPositionFunction(recyclerView),
            startDragAndDrop = createManagerStartDragAndDropFunction(recyclerView, itemTouchHelper),
            enableDragAndDrop = createManagerEnableDragAndDropFunction(itemTouchCallback),
            updateItemPadding = createManagerUpdateItemPaddingFunction(recyclerView),
            enableItemAnimations = createManagerEnableItemAnimationsFunction(recyclerView)
        )
    }

    private fun initDefaultChecklistItems() {
        if (isInEditMode) {
            setEditorItems(
                listOf(
                    TitleItem(
                        id = 1,
                        text = "Material Note Editor"
                    ),
                    ChecklistItemContainer(
                        id = 2,
                        formattedText = "[ ] Send meeting notes to team\n" +
                                "[ ] Order flowers\n" +
                                "[ ] Organise camera gear\n" +
                                "[ ] Book flights to Dubai\n" +
                                "[x] Lease out holiday home"
                    ),
                    ChipItemContainer(
                        id = 3,
                        items = listOf(
                            ChipItem(
                                id = 10,
                                text = "Important"
                            ),
                            ChipItem(
                                id = 11,
                                text = "TAX"
                            )
                        )
                    )
                )
            )
        }
    }

    /**
     * Creates a function for the checklist manager for scrolling to a checklist item at the provided position
     * in the recycler view.
     *
     * @param recyclerView The recycler view to use for scrolling to the checklist.
     */
    private fun createManagerScrollToPositionFunction(recyclerView: RecyclerView): (position: Int) -> Unit {
        return { position ->
            if (position != RecyclerView.NO_POSITION) {
                recyclerView.layoutManager?.scrollToPosition(position)
            }
        }
    }

    /**
     * Creates a function for the checklist manager for starting the drag-and-drop functionality
     * for a checklist item at the provided position in the recycler view.
     *
     * @param recyclerView The recycler view containing the checklist item to use as the target.
     * @param itemTouchHelper The item touch helper to use for starting the drag-and-drop functionality.
     */
    private fun createManagerStartDragAndDropFunction(
        recyclerView: RecyclerView,
        itemTouchHelper: ItemTouchHelper
    ): (position: Int) -> Unit {
        return { position ->
            val viewHolder = recyclerView.findViewHolderForAdapterPosition(position)
            if (viewHolder != null) {
                itemTouchHelper.startDrag(viewHolder)
            }
        }
    }

    /**
     * Creates a function for the checklist manager for enabling/disabling the drag-and-drop functionality
     * for checklist items.
     *
     * @param itemTouchCallback The item touch callback helper to use for enabling/disabling the drag-and-drop functionality.
     */
    private fun createManagerEnableDragAndDropFunction(itemTouchCallback: SimpleItemTouchHelper): (isEnabled: Boolean) -> Unit {
        return { isEnabled ->
            itemTouchCallback.setIsDragEnabled(isEnabled)
        }
    }

    /**
     * Creates a function for the checklist manager for updating the padding of the first and last checklist items
     * in the recycler view.
     *
     * @param recyclerView The recycler view containing the checklist items.
     */
    private fun createManagerUpdateItemPaddingFunction(recyclerView: RecyclerView): (firstItemTopPadding: Float?, lastItemBottomPadding: Float?) -> Unit {
        val defaultItemAnimator: RecyclerView.ItemAnimator? = recyclerView.itemAnimator

        return { firstItemTopPadding, lastItemBottomPadding ->
            for (index in 0 until recyclerView.itemDecorationCount) {
                val itemDecoration = recyclerView.getItemDecorationAt(index)
                if (itemDecoration is RecyclerSpaceItemDecorator) {
                    recyclerView.removeItemDecoration(itemDecoration)
                    break
                }
            }

            if (firstItemTopPadding != null || lastItemBottomPadding != null) {
                recyclerView.addItemDecoration(
                    if (defaultItemAnimator != null) {
                        RecyclerSpaceItemDecorator(
                            firstItemMargin = firstItemTopPadding?.toInt() ?: 0,
                            lastItemMargin = lastItemBottomPadding?.toInt() ?: 0,
                            defaultItemAnimator = defaultItemAnimator
                        )
                    } else {
                        RecyclerSpaceItemDecorator(
                            firstItemMargin = firstItemTopPadding?.toInt() ?: 0,
                            lastItemMargin = lastItemBottomPadding?.toInt() ?: 0
                        )
                    }
                )
            }
        }
    }

    /**
     * Creates a function for the checklist manager for enabling/disabling the item animations (add, remove, change, etc.)
     * of the recycler view.
     *
     * @param recyclerView The recycler view to use for enabling/disabling the item animations.
     */
    private fun createManagerEnableItemAnimationsFunction(recyclerView: RecyclerView): (isEnabled: Boolean) -> Unit {
        var itemAnimator: RecyclerView.ItemAnimator? = null

        return { isEnabled ->
            if (recyclerView.itemAnimator != null && recyclerView.itemAnimator != itemAnimator) {
                itemAnimator = recyclerView.itemAnimator
            }

            if (isEnabled) {
                recyclerView.itemAnimator = itemAnimator
            } else {
                recyclerView.itemAnimator = null
            }
        }
    }
}

private const val CHECKLIST_MENU_ITEM_CONVERT_TO_TEXT = 1
private const val CHECKLIST_MENU_ITEM_UNCHECK_ALL = 2
private const val CHECKLIST_MENU_ITEM_REMOVE_CHECKED = 3
private const val CHECKLIST_MENU_ITEM_DELETE_ITEM_WHEN_CHECKED = 4
