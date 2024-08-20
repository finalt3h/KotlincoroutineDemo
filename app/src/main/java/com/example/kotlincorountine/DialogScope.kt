package com.example.kotlincorountine

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.example.kotlincorountine.databinding.ActivityMainBinding
import com.example.kotlincorountine.databinding.FragmentFirstBinding
import com.example.kotlincorountine.listview.CountTimeAdapter
import com.example.kotlincorountine.listview.ItemCountTime
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class DialogScope : DialogFragment(), CoroutineScope by CoroutineScope(Dispatchers.Default) {


    var countTimeAdapter: CountTimeAdapter? = null
    private lateinit var binding: FragmentFirstBinding
    var list: List<ItemCountTime> = mutableListOf()
    fun startCount() {
        lifecycleScope.launch {
            repeat(15) { i ->
                delay(1000)
                Log.d(TAG, "startCount: $i")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.let {
            it.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
            it.setBackgroundDrawable(
                ColorDrawable(
                    ContextCompat.getColor(
                        requireContext(),
                        android.R.color.transparent
                    )
                )
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFirstBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnAdd.setOnClickListener {
            val positionStart = list.size
            list+= ItemCountTime()
            countTimeAdapter?.updateData(list)
            countTimeAdapter?.notifyItemRangeInserted(positionStart,1)
        }
        binding.btnStart.setOnClickListener {
            list.forEachIndexed { index, itemCountTime ->
                onStartItem(index, itemCountTime)
            }
        }
        binding.btnStopContinue.setOnClickListener {
            list.forEachIndexed { index, itemCountTime ->

            }
        }

        countTimeAdapter = CountTimeAdapter(object : CountTimeAdapter.Callback {
            override fun onStart(int: Int, item: ItemCountTime) {
                onStartItem(int, item)
            }

            override fun onStop(int: Int, item: ItemCountTime) {
                onStopItem(int, item)
            }

            override fun onContinue(int: Int, item: ItemCountTime) {
                onContinueItem(int, item)
            }

            override fun onReset(int: Int, item: ItemCountTime) {
                onResetItem(int, item)
            }

        })
        countTimeAdapter?.updateData(list)
        binding.listView.adapter = countTimeAdapter
    }

    fun onStartItem(pos: Int, item: ItemCountTime) {
        item.job?.cancel()
        item.time = 0
        item.job = lifecycleScope.launch(Dispatchers.Main) {
            while(true) {
                delay(10)
                item.time += 10
                countTimeAdapter?.notifyItemChanged(pos)
            }
        }
    }

    fun onStopItem(pos: Int, item: ItemCountTime) {
        item.job?.cancel()
        countTimeAdapter?.notifyItemChanged(pos)
    }

    fun onContinueItem(pos: Int, item: ItemCountTime) {
        item.job?.cancel()
        item.job = lifecycleScope.launch(Dispatchers.Main) {
            while(true) {
                delay(10)
                item.time += 10
                countTimeAdapter?.notifyItemChanged(pos)
            }
        }
    }

    fun onResetItem(pos: Int, item: ItemCountTime) {
        item.job?.cancel()
        item.time = 0
        countTimeAdapter?.notifyItemChanged(pos)

    }

    companion object {
        const val TAG = "DialogScope"
    }

}