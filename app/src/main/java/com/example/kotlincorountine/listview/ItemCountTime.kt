package com.example.kotlincorountine.listview

import kotlinx.coroutines.Job

data class ItemCountTime (
    var job: Job? = null,
    var time: Long = 0
)