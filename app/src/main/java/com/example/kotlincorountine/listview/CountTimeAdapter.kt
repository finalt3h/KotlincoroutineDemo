package com.example.kotlincorountine.listview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlincorountine.R
import com.example.kotlincorountine.databinding.ItemCountTimeBinding

class CountTimeAdapter(val callback: Callback) : RecyclerView.Adapter<CountTimeAdapter.ViewHolderItem>() {

    var list: List<ItemCountTime> = mutableListOf()
    fun updateData(items: List<ItemCountTime>) {
        list = items
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderItem {
        return ViewHolderItem(
            LayoutInflater.from(parent.context).inflate(R.layout.item_count_time, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun getItem(position: Int): ItemCountTime {
        return list[position]
    }

    override fun onBindViewHolder(holder: ViewHolderItem, position: Int) {
        holder.bindView(position, getItem(position))
    }

    inner class ViewHolderItem(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding: ItemCountTimeBinding = ItemCountTimeBinding.bind(itemView)
        fun bindView(position: Int, item: ItemCountTime) {
            binding.tvTime.text = item.time.toString()
            if (item.job?.isActive == true) {
                binding.btnStart.text = "Reset"
                binding.btnStop.text = "Stop"
                binding.btnStop.isEnabled = true
                binding.btnStart.setOnClickListener{
                    callback.onReset(position,item)
                }
                binding.btnStop.setOnClickListener{
                    callback.onStop(position,item)
                }
            } else {
                if (item.time == 0L) {
                    binding.btnStart.text = "Bắt đầu"
                    binding.btnStop.text = "Tiếp tục"
                    binding.btnStop.isEnabled = false
                    binding.btnStart.setOnClickListener{
                        callback.onStart(position,item)
                    }
                }else {
                    binding.btnStart.text = "Reset"
                    binding.btnStop.text = "Tiếp tục"
                    binding.btnStop.isEnabled = true
                    binding.btnStart.setOnClickListener{
                        callback.onReset(position,item)
                    }
                    binding.btnStop.setOnClickListener{
                        callback.onContinue(position,item)
                    }
                }
            }
        }
    }
    interface Callback{
        fun onStart(int: Int, item: ItemCountTime)
        fun onStop(int: Int, item: ItemCountTime)
        fun onContinue(int: Int, item: ItemCountTime)
        fun onReset(int: Int, item: ItemCountTime)
    }
}