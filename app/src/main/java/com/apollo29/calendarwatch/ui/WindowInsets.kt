package com.apollo29.calendarwatch.ui

import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding

private val SYSTEM_INSETS = WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()

/**
 * Edge-to-edge support: pads this view by the system bar / display cutout insets (on top of its
 * own padding), so the background still draws behind the system bars but the content does not.
 */
fun View.applySystemBarInsets(top: Boolean = true, bottom: Boolean = true) {
    val initialLeft = paddingLeft
    val initialTop = paddingTop
    val initialRight = paddingRight
    val initialBottom = paddingBottom
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, windowInsets ->
        val insets = windowInsets.getInsets(SYSTEM_INSETS)
        view.updatePadding(
            left = initialLeft + insets.left,
            top = initialTop + if (top) insets.top else 0,
            right = initialRight + insets.right,
            bottom = initialBottom + if (bottom) insets.bottom else 0
        )
        windowInsets
    }
}

/**
 * Edge-to-edge support for a toolbar with a fixed height: grows it by the status bar inset so its
 * background extends behind the status bar while its content stays below it.
 */
fun View.applyStatusBarInsetToToolbar() {
    val initialHeight = layoutParams.height
    val initialTop = paddingTop
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, windowInsets ->
        val top = windowInsets.getInsets(SYSTEM_INSETS).top
        view.updatePadding(top = initialTop + top)
        if (initialHeight >= 0) {
            view.updateLayoutParams<ViewGroup.LayoutParams> { height = initialHeight + top }
        }
        windowInsets
    }
}
