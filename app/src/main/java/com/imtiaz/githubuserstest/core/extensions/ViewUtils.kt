package com.imtiaz.githubuserstest.core.extensions

import android.app.Activity
import androidx.core.content.ContextCompat
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.imtiaz.githubuserstest.R

fun MaterialToolbar.setup(
    context: Activity,
    title: String = "",
    backgroundColor: Int = R.color.purple_500,
    textColor: Int = R.color.white,
    menu: Int? = null,
    isNavIconEnable: Boolean = true,
    onBackNavigation: () -> Unit = { context.onBackPressed() }
) {
    setTitle(title)
    setBackgroundColor(ContextCompat.getColor(context, backgroundColor))
    setTitleTextColor(ContextCompat.getColor(context, textColor))
    setNavigationIconTint(ContextCompat.getColor(context, textColor))
    setNavigationOnClickListener { onBackNavigation() }
    menu?.let { inflateMenu(it) }
    navigationIcon = if (isNavIconEnable) ContextCompat.getDrawable(
        context,
        R.drawable.ic_toolbar_back
    ) else null
    (this.layoutParams as AppBarLayout.LayoutParams).scrollFlags =
        AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or
                AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS or
                AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP
}