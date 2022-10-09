package com.imtiaz.githubuserstest.core.extensions

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class PaginateRecyclerview(
    recyclerView: RecyclerView,
    private val layoutManager: RecyclerView.LayoutManager?,
    private val startPaginate: () -> Unit = {}
) : RecyclerView.OnScrollListener() {

    init {
        recyclerView.addOnScrollListener(this)
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        when (layoutManager) {
            is GridLayoutManager -> {
                if (dy > 0 && layoutManager.findLastVisibleItemPosition() == layoutManager.itemCount - 1) {
                    startPaginate()
                }
            }
            is LinearLayoutManager -> {
                if (dy > 0 && layoutManager.findLastVisibleItemPosition() == layoutManager.itemCount - 1) {
                    startPaginate()
                }
            }
        }
    }
}