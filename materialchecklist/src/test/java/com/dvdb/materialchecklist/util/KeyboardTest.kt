package com.dvdb.materialchecklist.util

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*

class KeyboardTest {
    @Test fun `requesting keyboard twice never toggles it closed`() {
        val context = mock(Context::class.java)
        val view = mock(View::class.java)
        val manager = mock(InputMethodManager::class.java)
        `when`(view.context).thenReturn(context)
        `when`(context.getSystemService(Context.INPUT_METHOD_SERVICE)).thenReturn(manager)
        view.showKeyboard()
        view.showKeyboard()
        verify(manager, times(2)).showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        verify(manager, never()).toggleSoftInput(anyInt(), anyInt())
    }
}
