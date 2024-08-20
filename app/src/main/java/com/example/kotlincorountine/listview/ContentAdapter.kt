package com.example.kotlincorountine.listview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlincorountine.R
import com.example.kotlincorountine.databinding.ItemStaffBinding
import com.example.kotlincorountine.listview.ContentAdapter.ViewHolderItem
import java.util.Objects

class ContentAdapter(val callback: Callback) :
    ListAdapter<ContentModel, ViewHolderItem>(
        DiffCallback<ContentModel>(
            compareContentTheSame = { oldItem, newItem ->
                oldItem == newItem
            },
            getPayload = { oldItem, newItem ->
                val payloads: MutableList<String> = mutableListOf()
                if (oldItem.name != newItem.name) {
                    payloads.add(UPDATE_NAME)
                }
                if (oldItem.count != newItem.count) {
                    payloads.add(UPDATE_COUNT)
                }
                if (payloads.isEmpty()) {
                    null
                } else {
                    payloads
                }
            }
        )
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderItem {
        return ViewHolderItem(
            LayoutInflater.from(parent.context).inflate(R.layout.item_staff, parent, false)
        )
    }

    override fun onBindViewHolder(
        holder: ViewHolderItem,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.firstOrNull() != null) {
            holder.bindData(getItem(position),position, payloads.first() as MutableList<String>)
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    override fun onBindViewHolder(holder: ViewHolderItem, position: Int) {
        holder.bindView(position, getItem(position))
    }

    inner class ViewHolderItem(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding: ItemStaffBinding = ItemStaffBinding.bind(itemView)
        fun bindView(position: Int, item: ContentModel) {
            binding.btnAdd.setOnClickListener {
                callback?.apply { onAdd(position,item) }
            }
            updateName (item)
            updateCount (item)
        }

        fun bindData(
            data: ContentModel,
            position: Int,
            payloads: MutableList<String>
        ) {
            binding.btnAdd.setOnClickListener {
                callback?.apply { onAdd(position,data) }
            }
            payloads.forEach { payload ->
                when (payload) {
                    UPDATE_NAME -> {
                        updateName (item = data)
                    }
                    UPDATE_COUNT -> {
                        updateCount (item = data)
                    }
                }
            }
        }

        fun updateCount(item: ContentModel) {
            binding.tvCount.text = item.count.toString()
        }

        fun updateName(item: ContentModel) {
            binding.tvName.text = item.name
        }

    }

    interface Callback {
        fun onAdd(int: Int, item: ContentModel)
    }

    companion object {
        const val UPDATE_NAME = "UPDATE_NAME"
        const val UPDATE_COUNT = "UPDATE_COUNT"
    }
}

class DiffCallback<M>(
    private val compareTheSame: ((old: M, new: M) -> Boolean)? = null,
    private val compareContentTheSame: ((old: M, new: M) -> Boolean)? = null,
    private val getPayload: ((old: M, new: M) -> MutableList<String>?)? = null
) : DiffUtil.ItemCallback<M>() {
    override fun areItemsTheSame(oldItem: M & Any, newItem: M & Any): Boolean {
        return compareTheSame?.invoke(oldItem, newItem) ?: true
    }

    override fun areContentsTheSame(oldItem: M & Any, newItem: M & Any): Boolean {
        return compareContentTheSame?.invoke(oldItem, newItem) ?: Objects.equals(oldItem, newItem)
    }

    override fun getChangePayload(oldItem: M & Any, newItem: M & Any): Any? {
        return getPayload?.let { it(oldItem, newItem) }
    }
}
