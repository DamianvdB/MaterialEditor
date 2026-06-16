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

package com.dvdb.checklist.sample

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.preference.PreferenceManager
import com.dvdb.checklist.R
import com.dvdb.checklist.sample.config.ChecklistConfiguration
import com.dvdb.materialchecklist.config.checklist.*
import com.dvdb.materialchecklist.config.checklist.model.BehaviorCheckedItem
import com.dvdb.materialchecklist.config.chip.*
import com.dvdb.materialchecklist.config.content.setContentClickableLinks
import com.dvdb.materialchecklist.config.content.setContentHint
import com.dvdb.materialchecklist.config.content.setContentHintTextColor
import com.dvdb.materialchecklist.config.content.setContentLinkTextColor
import com.dvdb.materialchecklist.config.general.applyConfiguration
import com.dvdb.materialchecklist.config.general.setTextEditable
import com.dvdb.materialchecklist.config.image.*
import com.dvdb.materialchecklist.config.title.*
import com.dvdb.materialchecklist.manager.chip.model.ChipItem
import com.dvdb.materialchecklist.MaterialChecklist
import com.dvdb.materialchecklist.manager.image.model.ImageItem
import com.dvdb.materialchecklist.manager.model.*
import com.dvdb.materialchecklist.util.exhaustive
import com.google.android.material.snackbar.Snackbar
import com.dvdb.materialchecklist.R as MaterialChecklistR

private const val SETTINGS_ACTIVITY_REQUEST_CODE = 1000

private const val CHANGE_MENU_ITEM_VISIBILITY_DELAY_MS = 100L

private const val SP_CHECKLIST_ITEMS_TEXT_KEY = "mc_items_text"
private const val SP_SHOW_CHECKLIST_KEY = "mc_show_checklist"
private const val CHECKLIST_ITEMS_SAMPLE_TEXT = "[ ] Send meeting notes to team\n" +
        "[ ] Order flowers\n" +
        "[ ] Organise vacation photos\n" +
        "[ ] Book holiday flights\n" +
        "[ ] Scan vaccination certificates\n" +
        "[x] Advertise holiday home\n" +
        "[x] Wish Sarah happy birthday"

private const val SP_TITLE_ITEM_TEXT_KEY = "mc_item_title_text"
private const val TITLE_ITEM_SAMPLE_TEXT = "Admin Tasks"

private const val SP_CONTENT_ITEM_TEXT_KEY = "mc_item_content_text"

private const val D_NOTES_URL = "https://bit.ly/google_play_store_d_notes"
private const val GITHUB_URL = "https://bit.ly/github_material_checklist"

internal class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var checklistConfiguration: ChecklistConfiguration
    private lateinit var toast: Toast

    private var checklistItemsText: String = CHECKLIST_ITEMS_SAMPLE_TEXT
        get() = sharedPreferences.getString(SP_CHECKLIST_ITEMS_TEXT_KEY, field) ?: field
        set(value) {
            field = value
            sharedPreferences.edit().putString(SP_CHECKLIST_ITEMS_TEXT_KEY, value).apply()
        }

    private var titleItemText: String = TITLE_ITEM_SAMPLE_TEXT
        get() = sharedPreferences.getString(SP_TITLE_ITEM_TEXT_KEY, field) ?: field
        set(value) {
            field = value
            sharedPreferences.edit().putString(SP_TITLE_ITEM_TEXT_KEY, value).apply()
        }

    private var contentItemText: String = ""
        get() = sharedPreferences.getString(SP_CONTENT_ITEM_TEXT_KEY, field) ?: field
        set(value) {
            field = value
            sharedPreferences.edit().putString(SP_CONTENT_ITEM_TEXT_KEY, value).apply()
        }

    private var showNoteEditor: Boolean = true
        get() = sharedPreferences.getBoolean(SP_SHOW_CHECKLIST_KEY, field)
        set(value) {
            field = value
            sharedPreferences.edit().putBoolean(SP_SHOW_CHECKLIST_KEY, value).apply()
        }

    private lateinit var convertToTextMenuItem: MenuItem
    private lateinit var convertToChecklistMenuItem: MenuItem
    private lateinit var removeCheckedItemsMenuItem: MenuItem
    private lateinit var uncheckCheckedItemsMenuItem: MenuItem
    private lateinit var toggleDeleteCheckedItemsMenuItem: MenuItem
    private lateinit var mainRoot: View
    private lateinit var mainChecklist: MaterialChecklist
    private lateinit var mainText: EditText

    @SuppressLint("ShowToast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        checklistConfiguration = ChecklistConfiguration(this, sharedPreferences)
        toast = Toast.makeText(this, "", Toast.LENGTH_SHORT)

        initView()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main_activity, menu)

        convertToTextMenuItem =
            menu!!.findItem(R.id.menu_main_activity_convert_to_text).apply {
                isVisible = showNoteEditor
            }

        removeCheckedItemsMenuItem =
            menu.findItem(R.id.menu_main_activity_remove_checked_items).apply {
                isVisible = showNoteEditor
            }

        uncheckCheckedItemsMenuItem =
            menu.findItem(R.id.menu_main_activity_uncheck_checked_items).apply {
                isVisible = showNoteEditor
            }

        toggleDeleteCheckedItemsMenuItem =
            menu.findItem(R.id.menu_main_activity_toggle_delete_checked_items).apply {
                isVisible = showNoteEditor
                updateDeleteCheckedItemsMenuState(checklistConfiguration.behaviorCheckedItem)
            }

        convertToChecklistMenuItem =
            menu.findItem(R.id.menu_main_activity_convert_to_checklist).apply {
                isVisible = !showNoteEditor
            }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_main_activity_convert_to_text -> handleOnConvertToTextMenuItemClicked()
            R.id.menu_main_activity_convert_to_checklist -> handleOnConvertToChecklistMenuItemClicked()
            R.id.menu_main_activity_remove_checked_items -> handleOnRemoveCheckedItemsMenuItemClicked()
            R.id.menu_main_activity_uncheck_checked_items -> handleOnUncheckCheckedItemsMenuItemClicked()
            R.id.menu_main_activity_toggle_delete_checked_items -> handleOnToggleDeleteCheckedItemsMenuItemClicked()
            R.id.menu_main_activity_d_notes -> handleOnDNotesMenuItemClicked()
            R.id.menu_main_activity_github -> handleOnGithubMenuItemClicked()
            R.id.menu_main_activity_settings -> handleOnSettingsMenuItemClicked()
            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == SETTINGS_ACTIVITY_REQUEST_CODE) {
            handleSettingConfiguration()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onStop() {
        if (showNoteEditor) {
            val items = mainChecklist.getEditorItems()

            items.forEach { item ->
                when (item) {
                    is TitleItem -> {
                        titleItemText = item.text
                    }
                    is ContentItem -> {
                        contentItemText = item.text
                    }
                    is ChecklistItemContainer -> {
                        checklistItemsText = item.formattedText
                    }
                    is ImageItemContainer,
                    is ChipItemContainer -> {
                    }
                }.exhaustive
            }
        } else {
            checklistItemsText = mainText.text.toString()
        }

        super.onStop()
    }

    private fun initView() {
        mainRoot = findViewById(R.id.main_root)
        mainChecklist = findViewById(R.id.main_checklist)
        mainText = findViewById(R.id.main_text)

        initChecklist()
        initChips()
        initImages()

        if (showNoteEditor) {
            setItems()
        } else {
            mainText.setText(checklistItemsText)

            mainText.visibility = View.VISIBLE
            mainChecklist.visibility = View.GONE
        }

        handleSettingConfiguration()
    }

    private fun initChecklist() {
        mainChecklist.setOnItemDeletedListener { text, id ->
            val message: String =
                if (text.isNotEmpty()) "Checklist item deleted: \"$text\"" else "Checklist item deleted"
            Snackbar.make(mainRoot, message, Snackbar.LENGTH_LONG)
                .setAction("undo") {
                    mainChecklist.restoreDeletedItem(id)
                }.show()
        }
    }

    private fun initChips() {
        mainChecklist.setOnChipItemClicked {
            toast.setText("Chip item clicked with text '${it.text}'")
            toast.show()
        }

        mainChecklist.setOnChipItemLongClicked {
            toast.setText("Chip item long clicked with text '${it.text}'")
            toast.show()
            true
        }
    }

    private fun initImages() {
        mainChecklist.setOnImageItemClicked {
            toast.setText("Image clicked with id '${it.id}'")
            toast.show()
        }

        mainChecklist.setOnImageItemLongClicked {
            toast.setText("Image long clicked with id '${it.id}'")
            toast.show()
            true
        }
    }

    private fun setItems() {
        mainChecklist.setEditorItems(
            listOf(
                ImageItemContainer(
                    1,
                    generateImageItems()
                ),
                TitleItem(
                    2,
                    titleItemText
                ),
                ContentItem(
                    3,
                    contentItemText
                ),
                ChipItemContainer(
                    4,
                    generateChipItems()
                ),
                ChecklistItemContainer(
                    5,
                    checklistItemsText
                )
            )
        )
    }

    private fun generateChipItems(): List<ChipItem> {
        val important = SpannableString("Important").apply {
            setSpan(
                StrikethroughSpan(),
                0,
                this.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        return listOf(
            ChipItem(1, important, R.drawable.ic_baseline_access_alarm_24),
            ChipItem(2, "Errands", R.drawable.ic_baseline_access_time_24),
            ChipItem(3, "Admin"),
            ChipItem(4, "Work"),
            ChipItem(5, "Groceries")
        )
    }

    private fun generateImageItems(): List<ImageItem> {
        return listOf(
            ImageItem(
                id = 1,
                text = "Holiday booking",
                attachmentDisplayName = "Holiday booking.pdf",
                attachmentMimeType = "application/pdf",
                attachmentSizeLabel = "128 KB",
                attachmentDateLabel = "Today",
                attachmentActionLabel = "Open",
                primaryImage = ContextCompat.getDrawable(this, MaterialChecklistR.drawable.ic_add).apply {
                    this!!.mutate()

                    DrawableCompat.setTintList(this, null)

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        DrawableCompat.setTint(this, Color.BLACK)
                    } else {
                        DrawableCompat.setTint(DrawableCompat.wrap(this), Color.BLACK)
                    }
                }
            ),
            ImageItem(
                id = 2,
                text = "Alarm recording",
                attachmentDisplayName = "Alarm recording.m4a",
                attachmentMimeType = "audio/mp4",
                attachmentSizeLabel = "42 KB",
                attachmentDateLabel = "Yesterday",
                attachmentActionLabel = "Play",
                secondaryImage = ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_baseline_access_time_24
                ).apply {
                    this!!.mutate()

                    DrawableCompat.setTintList(this, null)

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        DrawableCompat.setTint(this, Color.BLACK)
                    } else {
                        DrawableCompat.setTint(DrawableCompat.wrap(this), Color.BLACK)
                    }
                }
            ),
            ImageItem(
                id = 3,
                primaryImageUri = generateUriFromDrawableResource(R.drawable.bg_sun_mountain)
            ),
            ImageItem(
                id = 4,
                primaryImageUri = generateUriFromDrawableResource(R.drawable.bg_sun_mountain)
            ),
            ImageItem(
                id = 5,
                secondaryImageUri = generateUriFromDrawableResource(R.drawable.bg_sun_mountain)
            )
        )
    }

    private fun handleSettingConfiguration() {
        handleSettingChecklistConfiguration()
        handleSettingTitleConfiguration()
        handleSettingContentConfiguration()
        handleSettingChipConfiguration()
        handleSettingGeneralConfiguration()
        handleSettingImageConfiguration()

        mainChecklist.applyConfiguration()
    }

    private fun handleSettingChecklistConfiguration() {
        // Apply text settings from config to checklist system
        mainChecklist.setTextColor(checklistConfiguration.textColor)
            .setLinkTextColor(checklistConfiguration.textLinkTextColor)
            .setTextSize(checklistConfiguration.textSize)
            .setNewItemText(checklistConfiguration.textNewItem)
            .setCheckedItemTextAlpha(checklistConfiguration.textAlphaCheckedItem)
            .setNewItemTextAlpha(checklistConfiguration.textAlphaNewItem)
            .setLinksClickable(checklistConfiguration.textLinksClickable)

        checklistConfiguration.textTypeFace?.let {
            mainChecklist.setTextTypeFace(it)
        }

        // Apply icon settings from config to checklist system
        mainChecklist.setIconTintColor(checklistConfiguration.iconTintColor)
            .setDragIndicatorIconAlpha(checklistConfiguration.iconAlphaDragIndicator)
            .setDeleteIconAlpha(checklistConfiguration.iconAlphaDelete)
            .setAddIconAlpha(checklistConfiguration.iconAlphaAdd)

        // Apply checkbox settings from config to checklist system
        checklistConfiguration.checkboxTintColor?.let {
            mainChecklist.setCheckboxTintColor(it)
        }

        mainChecklist.setCheckedItemCheckboxAlpha(checklistConfiguration.checkboxAlphaCheckedItem)

        // Apply drag-and-drop settings from config to checklist system
        mainChecklist.setDragAndDropToggleBehavior(checklistConfiguration.dragAndDropToggleBehavior)
            .setDragAndDropDismissKeyboardBehavior(checklistConfiguration.dragAndDismissKeyboardBehavior)

        checklistConfiguration.dragAndDropActiveItemBackgroundColor?.let {
            mainChecklist.setDragAndDropItemActiveBackgroundColor(it)
        }

        // Apply item behavior settings from config to checklist system
        mainChecklist.setOnItemCheckedBehavior(checklistConfiguration.behaviorCheckedItem)
            .setOnItemUncheckedBehavior(checklistConfiguration.behaviorUncheckedItem)
        updateDeleteCheckedItemsMenuState(checklistConfiguration.behaviorCheckedItem)

        // Apply item settings from config to checklist system
        checklistConfiguration.itemFirstTopPadding?.let {
            mainChecklist.setItemFirstTopPadding(it)
        }

        checklistConfiguration.itemLeftAndRightPadding?.let {
            mainChecklist.setItemLeftAndRightPadding(it)
        }

        checklistConfiguration.itemTopAndBottomPadding?.let {
            mainChecklist.setItemTopAndBottomPadding(it)
        }

        checklistConfiguration.itemLastBottomPadding?.let {
            mainChecklist.setItemLastBottomPadding(it)
        }

        // Apply text settings from config to edit text
        mainText.setTextColor(checklistConfiguration.textColor)
        mainText.setTextSize(TypedValue.COMPLEX_UNIT_PX, checklistConfiguration.textSize)
        checklistConfiguration.textTypeFace?.let {
            mainText.typeface = it
        }
    }

    private fun handleSettingTitleConfiguration() {
        mainChecklist.setTitleHint(checklistConfiguration.titleHint)
            .setTitleTextColor(checklistConfiguration.titleTextColor)
            .setTitleLinkTextColor(checklistConfiguration.titleLinkTextColor)
            .setTitleHintTextColor(checklistConfiguration.titleHintTextColor)
            .setTitleClickableLinks(checklistConfiguration.titleClickableLinks)
            .setTitleShowActionIcon(checklistConfiguration.titleShowActionIcon)
    }

    private fun handleSettingContentConfiguration() {
        mainChecklist.setContentHint(checklistConfiguration.contentHint)
            .setContentLinkTextColor(checklistConfiguration.contentLinkTextColor)
            .setContentHintTextColor(checklistConfiguration.contentHintTextColor)
            .setContentClickableLinks(checklistConfiguration.contentClickableLinks)
    }

    private fun handleSettingChipConfiguration() {
        mainChecklist.setChipBackgroundColor(checklistConfiguration.chipBackgroundColor)
        mainChecklist.setChipStrokeColor(checklistConfiguration.chipStrokeColor)
        mainChecklist.setChipStrokeWidth(checklistConfiguration.chipStrokeWidth)
        mainChecklist.setChipIconSize(checklistConfiguration.chipIconSize)

        checklistConfiguration.chipIconEndPadding?.let {
            mainChecklist.setChipIconEndPadding(it)
        }

        mainChecklist.setChipMinHeight(checklistConfiguration.chipMinHeight)
            .setChipHorizontalSpacing(checklistConfiguration.chipHorizontalSpacing)
            .setChipInternalLeftAndRightPadding(checklistConfiguration.chipLeftAndRightInternalPadding)
    }

    private fun handleSettingGeneralConfiguration() {
        mainChecklist.setTextEditable(checklistConfiguration.textEditable)
    }

    private fun handleSettingImageConfiguration() {
        mainChecklist.setImageMaxColumnSpan(checklistConfiguration.imageMaxColumnSpan)

        checklistConfiguration.imageTextColor?.let {
            mainChecklist.setImageTextColor(it)
        }

        mainChecklist.setImageStrokeColor(checklistConfiguration.imageStrokeColor)
            .setImageStrokeWidth(checklistConfiguration.imageStrokeWidth)
            .setImageTextSize(checklistConfiguration.imageTextSize)
            .setImageCornerRadius(checklistConfiguration.imageCornerRadius)
            .setImageInnerPadding(checklistConfiguration.imageInnerPadding)

        mainChecklist.setImageLeftAndRightPadding(checklistConfiguration.imageLeftAndRightPadding)

        mainChecklist.setImageTopAndBottomPadding(checklistConfiguration.imageTopAndBottomPadding)

        mainChecklist.setImageAdjustItemTextSize(checklistConfiguration.imageAdjustItemTextSize)
    }

    private fun handleOnConvertToChecklistMenuItemClicked() {
        updateVisibleContentOnConvertMenuItemClicked(true)
    }

    private fun handleOnConvertToTextMenuItemClicked() {
        updateVisibleContentOnConvertMenuItemClicked(false)
    }

    private fun updateVisibleContentOnConvertMenuItemClicked(isConvertToChecklistMenuItemClicked: Boolean) {
        showNoteEditor = isConvertToChecklistMenuItemClicked

        mainRoot?.handler?.postDelayed(
            {
                convertToTextMenuItem.isVisible = isConvertToChecklistMenuItemClicked
                removeCheckedItemsMenuItem.isVisible = isConvertToChecklistMenuItemClicked
                uncheckCheckedItemsMenuItem.isVisible = isConvertToChecklistMenuItemClicked
                toggleDeleteCheckedItemsMenuItem.isVisible = isConvertToChecklistMenuItemClicked

                convertToChecklistMenuItem.isVisible = !isConvertToChecklistMenuItemClicked
            },
            CHANGE_MENU_ITEM_VISIBILITY_DELAY_MS
        )

        if (isConvertToChecklistMenuItemClicked) {
            checklistItemsText = mainText.text.toString()
            setItems()
        } else {
            val items = mainChecklist.getEditorItems()

            items.forEach { item ->
                when (item) {
                    is TitleItem -> {
                        titleItemText = item.text
                    }
                    is ContentItem -> {
                        contentItemText = item.text
                    }
                    is ChecklistItemContainer,
                    is ChipItemContainer,
                    is ImageItemContainer -> Unit
                }
            }

            val content: String = mainChecklist.getItems()
            mainText.setText(content)
        }

        mainText.visibility = if (isConvertToChecklistMenuItemClicked) {
            View.GONE
        } else {
            View.VISIBLE
        }

        mainChecklist.visibility = if (isConvertToChecklistMenuItemClicked) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun handleOnRemoveCheckedItemsMenuItemClicked() {
        val itemIdsOfRemovedItems =
            mainChecklist.removeAllCheckedItems()

        val message = resources.getQuantityString(
            R.plurals.item_checked_removed,
            itemIdsOfRemovedItems.size,
            itemIdsOfRemovedItems.size
        )

        Snackbar.make(mainRoot, message, Snackbar.LENGTH_LONG).apply {
            if (itemIdsOfRemovedItems.isNotEmpty()) {
                setAction("undo") {
                    mainChecklist.restoreDeletedItems(itemIdsOfRemovedItems)
                }
            }
        }.show()
    }

    private fun handleOnUncheckCheckedItemsMenuItemClicked() {
        mainChecklist.uncheckAllCheckedItems()
    }

    private fun handleOnToggleDeleteCheckedItemsMenuItemClicked() {
        val behavior = if (toggleDeleteCheckedItemsMenuItem.isChecked) {
            BehaviorCheckedItem.MOVE_TO_TOP_OF_CHECKED_ITEMS
        } else {
            BehaviorCheckedItem.DELETE
        }

        mainChecklist
            .setOnItemCheckedBehavior(behavior)
            .applyConfiguration()
        updateDeleteCheckedItemsMenuState(behavior)
    }

    private fun updateDeleteCheckedItemsMenuState(behavior: BehaviorCheckedItem) {
        if (::toggleDeleteCheckedItemsMenuItem.isInitialized) {
            toggleDeleteCheckedItemsMenuItem.isChecked = behavior == BehaviorCheckedItem.DELETE
            toggleDeleteCheckedItemsMenuItem.title = if (toggleDeleteCheckedItemsMenuItem.isChecked) {
                getString(R.string.menu_keep_checked_items)
            } else {
                getString(R.string.menu_delete_checked_items)
            }
        }
    }

    private fun handleOnDNotesMenuItemClicked() {
        openUrl(D_NOTES_URL)
    }

    private fun handleOnGithubMenuItemClicked() {
        openUrl(GITHUB_URL)
    }

    private fun openUrl(url: String) {
        dismissKeyboard()

        startActivity(
            Intent(Intent.ACTION_VIEW)
                .setData(Uri.parse(url))
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    private fun handleOnSettingsMenuItemClicked() {
        dismissKeyboard()

        startActivityForResult(
            Intent(this, SettingsActivity::class.java),
            SETTINGS_ACTIVITY_REQUEST_CODE
        )
    }

    private fun dismissKeyboard() {
        (this.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)
            ?.hideSoftInputFromWindow(window.decorView.windowToken, 0)
    }

    @Suppress("SameParameterValue")
    private fun generateUriFromDrawableResource(@DrawableRes drawableRes: Int): Uri {
        return Uri.parse(
            ContentResolver.SCHEME_ANDROID_RESOURCE +
                    "://" + resources.getResourcePackageName(drawableRes) +
                    '/' + resources.getResourceTypeName(drawableRes) +
                    '/' + resources.getResourceEntryName(drawableRes)
        )
    }
}
