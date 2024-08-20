package com.example.kotlincorountine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlincorountine.listview.ContentModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlin.random.Random

class ListStaffVM : ViewModel() {
    private var _listFlow: MutableStateFlow<List<ContentModel>> = MutableStateFlow(mutableListOf())
    val listFlow = _listFlow.asStateFlow() // chỉ cho phép đọc dữ liệu
    private var _isRefreshSuccess = Channel<Boolean>()
    val isRefreshSuccess = _isRefreshSuccess.receiveAsFlow() // chuyển đổi thành hot flow
    private var _showError = Channel<String>()
    val showError = _showError.receiveAsFlow() // chuyển đổi thành hot flow
    val _isShowLoading :MutableSharedFlow<Boolean> = MutableSharedFlow<Boolean>(
        0,
        1,
        BufferOverflow.SUSPEND
    )
    val isShowLoading = _isShowLoading.asSharedFlow()
    companion object {
        const val MAX_ITEM = 10
    }

    init {
        _isShowLoading.tryEmit(true)
    }
    fun reloadData() {
        flowReloadData(true)
            .onEach { result ->
                result.onSuccess {
                    _listFlow.value = it
                    _isRefreshSuccess.send(true)
                }.onFailure {
                    _showError.send(it.message?:"")
                    _isRefreshSuccess.send(true)
                }
            }.launchIn(viewModelScope)
    }
    // isSuccess đề fake api
    fun addData(isSuccess: Boolean) {
        flowOf(_listFlow.value)
            .filter { it.count() < MAX_ITEM }
            .flatMapLatest {
                _isShowLoading.emit(true)
                flowAddName(isSuccess)
            }
            .onEach { result ->
                _isShowLoading.emit(false)
                result.onSuccess {
                    _listFlow.value += it
                }.onFailure {
                    _showError.send(it.message?:"")
                }
            }.launchIn(viewModelScope)
    }

    fun addCountData(pos: Int) {
        val item = _listFlow.value[pos]
        var listNew = _listFlow.value.toMutableList()
        listNew[pos] = item.copy().apply { count++ }
        _listFlow.value = listNew
    }

    // isSuccess đề fake api
    private fun flowAddName(isSuccess: Boolean): Flow<Result<ContentModel>> {
        return flow {
            delay(1000)
            val name = ContentModel(getDefaultName())
            if (isSuccess) {
                emit(Result.success(name))
            } else {
                emit(Result.failure(Throwable("Lỗi xảy ra !")))
            }

        }
    }

    // isSuccess đề fake api
    private fun flowReloadData(isSuccess: Boolean): Flow<Result<List<ContentModel>>> {
        return flow {
            countName=0
            delay(1000)
            val count = Random.nextInt(3,10)
            val list : MutableList<ContentModel> = mutableListOf()
            for (i in 0..count) {
                list.add(ContentModel(getDefaultName()))
            }
            if (isSuccess) {
                emit(Result.success(list))
            } else {
                emit(Result.failure(Throwable("Lỗi xảy ra !")))
            }

        }
    }


    var countName = 0
    fun getDefaultName(): String {
        countName++
        return "Nguyễn Văn $countName"
    }
}