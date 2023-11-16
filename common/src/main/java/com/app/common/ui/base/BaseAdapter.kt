package com.app.common.ui.base

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.app.core.extensions.isNotNullOrEmpty

abstract class BaseAdapter<T, VH : ViewBinding> : RecyclerView.Adapter<BaseAdapter.ViewHolder<VH>>() {

    protected val currentList = ArrayList<T>()
    open fun getList() : List<T> = currentList

    open fun submitList(list: List<T>?) {
        if (list.isNotNullOrEmpty()) {
            currentList.clear()
            currentList.addAll(list!!)
            notifyDataSetChanged()
        }
    }


    fun getItem(position: Int) : T = currentList[position]

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<VH> {
        return ViewHolder(createdBinding(parent, viewType))
    }

    override fun onBindViewHolder(holder: ViewHolder<VH>, position: Int) {
        val binding = holder.binding
        val item = getItem(position)
        bind(binding, item)
        bindHolder(holder, position)
    }

    override fun getItemCount(): Int = currentList.size

    open fun bind(binding: VH, item: T) {}
    open fun bindHolder(holder: ViewHolder<VH>, position: Int) {}

    abstract fun createdBinding(parent: ViewGroup, viewType: Int) : VH

    class ViewHolder<out T : ViewBinding>(val binding: T) : RecyclerView.ViewHolder(binding.root)
}