package com.dvdb.materialchecklist.recycler.util.holder

import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*

class EnterActionPerformedFactoryTest {
    private val view = mock(TextView::class.java)
    private fun event(action: Int, keyCode: Int = KeyEvent.KEYCODE_ENTER, modifiers: Boolean = false): KeyEvent =
        mock(KeyEvent::class.java).also {
            `when`(it.action).thenReturn(action)
            `when`(it.keyCode).thenReturn(keyCode)
            `when`(it.hasNoModifiers()).thenReturn(!modifiers)
        }

    @Test fun `IME next with enter event inserts once and consumes key up`() {
        var insertions = 0
        val listener = EnterActionPerformedFactory().create({ insertions++; true })
        assertTrue(listener.onEditorAction(view, EditorInfo.IME_ACTION_NEXT, event(KeyEvent.ACTION_DOWN)))
        assertTrue(listener.onEditorAction(view, EditorInfo.IME_ACTION_NEXT, event(KeyEvent.ACTION_UP)))
        assertEquals(1, insertions)
    }

    @Test fun `software next and done without events are handled`() {
        var insertions = 0
        val listener = EnterActionPerformedFactory().create({ insertions++; true })
        assertTrue(listener.onEditorAction(view, EditorInfo.IME_ACTION_NEXT, null))
        assertTrue(listener.onEditorAction(view, EditorInfo.IME_ACTION_DONE, null))
        assertEquals(2, insertions)
    }

    @Test fun `physical enter handles only one key down`() {
        var insertions = 0
        val listener = EnterActionPerformedFactory().create({ insertions++; true })
        assertTrue(listener.onEditorAction(view, EditorInfo.IME_NULL, event(KeyEvent.ACTION_DOWN)))
        assertTrue(listener.onEditorAction(view, EditorInfo.IME_NULL, event(KeyEvent.ACTION_UP)))
        assertEquals(1, insertions)
    }

    @Test fun `unrelated and modified keys do not insert`() {
        var insertions = 0
        val listener = EnterActionPerformedFactory().create({ insertions++; true })
        assertFalse(listener.onEditorAction(view, EditorInfo.IME_ACTION_SEARCH, null))
        assertFalse(listener.onEditorAction(view, EditorInfo.IME_NULL, event(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_A)))
        assertFalse(listener.onEditorAction(view, EditorInfo.IME_NULL, event(KeyEvent.ACTION_DOWN, modifiers = true)))
        assertEquals(0, insertions)
    }

    @Test fun `unfocused items are ignored`() {
        var insertions = 0
        val listener = EnterActionPerformedFactory().create({ insertions++; true }, { true })
        assertFalse(listener.onEditorAction(view, EditorInfo.IME_ACTION_NEXT, null))
        assertEquals(0, insertions)
    }
}
