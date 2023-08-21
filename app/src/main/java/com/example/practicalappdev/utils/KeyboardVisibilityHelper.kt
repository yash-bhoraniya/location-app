package com.example.practicalappdev.utils

import android.graphics.Rect
import android.view.View
import android.view.ViewTreeObserver

class KeyboardVisibilityHelper(private val rootView: View) {
    private var isKeyboardVisible = false
    private var keyboardVisibilityListener: ((Boolean) -> Unit)? = null

    init {
        rootView.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            rootView.getWindowVisibleDisplayFrame(rect)
            val screenHeight = rootView.rootView.height
            val keypadHeight = screenHeight - rect.bottom

            val isKeyboardVisible = keypadHeight > screenHeight * 0.15
            if (this.isKeyboardVisible != isKeyboardVisible) {
                this.isKeyboardVisible = isKeyboardVisible
                keyboardVisibilityListener?.invoke(isKeyboardVisible)
            }
        }
    }

    fun setKeyboardVisibilityListener(listener: (Boolean) -> Unit) {
        keyboardVisibilityListener = listener
    }
}