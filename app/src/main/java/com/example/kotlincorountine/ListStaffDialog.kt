package com.example.kotlincorountine

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.viewModelScope
import com.example.kotlincorountine.databinding.ListStaffFragmentBinding
import com.example.kotlincorountine.listview.ContentAdapter
import com.example.kotlincorountine.listview.ContentModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ListStaffDialog : DialogFragment() {
    private var countTimeAdapter: ContentAdapter? = null
    private lateinit var binding: ListStaffFragmentBinding
    private lateinit var viewModel: ListStaffVM
    private lateinit var dialogLoading: LoadingDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ListStaffVM()
        dialogLoading = LoadingDialog()
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
        binding = ListStaffFragmentBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        setupEvent()
        onSubscribe()
    }

    private fun initView() {
        countTimeAdapter = ContentAdapter(object : ContentAdapter.Callback {
            override fun onAdd(pos: Int, item: ContentModel) {
                viewModel.addCountData(pos = pos)
            }

        })
        binding.listView.adapter = countTimeAdapter
    }

    private fun setupEvent() {
        binding.btnAdd.setOnClickListener {
            viewModel.addData(true)
        }
        binding.btnAdd2.setOnClickListener {
            viewModel.addData(false)
        }
        binding.refeshView.setOnRefreshListener {
            viewModel.reloadData()
        }

    }

    private fun onSubscribe() {
        viewModel.listFlow
            .flowWithLifecycle(
                viewLifecycleOwner.lifecycle,
                Lifecycle.State.STARTED
            ) // setup flow bắt đầu nhận ở trạng thái stated của vòng đời
            .onEach {
                println("Tesst-- $it")
                countTimeAdapter?.submitList(it)
            }.launchIn(viewModel.viewModelScope)
        viewModel.isShowLoading
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach {
                if (it) {
                    showLoading()
                } else {
                    hideLoading()
                }
            }.launchIn(viewModel.viewModelScope)
        viewModel.isRefreshSuccess
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach {
                binding.refeshView.isRefreshing = false
            }.launchIn(viewModel.viewModelScope)
        viewModel.showError
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach {
                Toast.makeText(requireActivity(), it, Toast.LENGTH_SHORT).show()
            }.launchIn(viewModel.viewModelScope)
    }


    fun showLoading() {
        dialogLoading.show(childFragmentManager, "dialog")
//        dialog.show()
    }

    fun hideLoading() {
//        dialog/.hide()
        dialogLoading.dismiss()
    }

    companion object {
        const val TAG = "DialogScope"
    }

}