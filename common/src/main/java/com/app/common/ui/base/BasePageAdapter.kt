package com.app.common.ui.base

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by Jumadi Janjaya date on 19/07/2020.
 * Bengkulu, Indonesia.
 * Copyright (c) Jumbox. All rights reserved.
 **/

abstract class BasePageAdapter<T : Any, VH : ViewDataBinding>(diffCallback: DiffUtil.ItemCallback<T>) : PagingDataAdapter<T, BasePageAdapter.ViewHolder<VH>>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<VH> {
        return ViewHolder(createdBinding(parent, viewType))
    }

    override fun onBindViewHolder(holder: ViewHolder<VH>, position: Int) {
        val binding = holder.binding
        val item = getItem(position)
        if (item != null) {
            bind(binding, item)
        }
        bindHolder(holder, position)
        holder.binding.executePendingBindings()
    }

    open fun bind(binding: VH, item: T) {}
    open fun bindHolder(holder: ViewHolder<VH>, position: Int) {}

    abstract fun createdBinding(parent: ViewGroup, viewType: Int) : VH

    class ViewHolder<out T : ViewDataBinding>(val binding: T) : RecyclerView.ViewHolder(binding.root)

}