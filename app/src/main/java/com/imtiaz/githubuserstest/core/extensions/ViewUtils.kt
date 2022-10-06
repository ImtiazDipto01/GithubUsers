package com.imtiaz.githubuserstest.core.extensions

import android.app.Activity
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
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
}

fun ImageView.loadImage(url: String?) {
    val options: RequestOptions = RequestOptions()
        .transform(FitCenter())
        .placeholder(R.drawable.placeholder)
        .error(R.drawable.placeholder)
        .priority(Priority.HIGH)

    Glide.with(this)
        .load(url)
        .apply(options)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .into(this)
}