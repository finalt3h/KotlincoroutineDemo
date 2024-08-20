package com.example.kotlincorountine

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.kotlincorountine.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.dropWhile
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlin.system.measureTimeMillis

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    val channel = Channel<String>()
    var count = 1
    var flowListItem: MutableStateFlow<List<String>> = MutableStateFlow(mutableListOf())


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        binding.btnRun.setOnClickListener {
            testZip()

            val pkgTag = listOf("1","2","3","23")
            val tagSpecialList =
                ArrayList(mutableListOf("7", "1", "42"))

//            createFlow().onEach {
//                println("$TAG_DEBUG - $it")
//            }.launchIn(GlobalScope)
//            println("$TAG_DEBUG - Xin chào")
        }
        binding.btnShow.setOnClickListener {
            binding.progressBar.isVisible = true

        }
        binding.btnHide.setOnClickListener {
            binding.progressBar.isVisible = false
        }
        binding.btnDialog.setOnClickListener {
            val manager = supportFragmentManager
            val dialog = ListStaffDialog()
            dialog.show(manager, null)
        }
        binding.btnAdd.setOnClickListener {
            flowListItem.value += count.toString()
            channel.trySend(count.toString())
            count++
        }
        binding.btnAddOs.setOnClickListener {
            println("$TAG_DEBUG btnAddOs- ${channel}")
            flowListItem.onEach {
                println("$TAG_DEBUG OS- $it")
            }.onCompletion {
                println("$TAG_DEBUG OS- onCompletion")
            }.launchIn(GlobalScope)
        }

        flowListItem.onEach {
            println("$TAG_DEBUG - $it")
        }.onCompletion {
            println("$TAG_DEBUG - onCompletion")
        }.launchIn(GlobalScope)
        channel.consumeAsFlow().onEach {
            println("$TAG_DEBUG - consumeAsFlow - $it")
        }.launchIn(GlobalScope)
        channel.receiveAsFlow().onEach {
            println("$TAG_DEBUG - receiveAsFlow - $it")
        }.launchIn(GlobalScope)
    }

    // 1. introduce kotlin corountine
    fun testConfigCorountine() {
        GlobalScope.launch(Dispatchers.IO) { // Dispatchers định nghĩa corountine chay trên theard nào
            delay(1000L) // non-blocking coroutine bị delay 5s
            println("World,") // print từ World ra sau khi hết delay
        }
        println("Hello,") // main thread vẫn tiếp tục chạy xuống dòng code này trong khi coroutine vẫn đang bị delay 10s
        Thread.sleep(3000L) // block main thread 3s
        println("Kotlin") // sau khi block thread 3s th sẽ in ra
    }

    // 2.
    fun testDispatchersCoroutines() {
        GlobalScope.launch() {
            Log.d(TAG_DEBUG, "Dispatchers NONE: run on ${Thread.currentThread().name}")
        }
        GlobalScope.launch(Dispatchers.Default) {
            Log.d(TAG_DEBUG, "Dispatchers Default: run on ${Thread.currentThread().name}")
        }
        GlobalScope.launch(Dispatchers.Main) {
            Log.d(TAG_DEBUG, "Dispatchers Main: run on ${Thread.currentThread().name}")
        }
        GlobalScope.launch(Dispatchers.IO) {
            Log.d(TAG_DEBUG, "Dispatchers IO: run on ${Thread.currentThread().name}")
        }
        GlobalScope.launch(CoroutineName("My Name")) {
            Log.d(TAG_DEBUG, "Dispatchers CoroutineName: run on ${Thread.currentThread().name}")
        }
        GlobalScope.launch(Dispatchers.Unconfined) {
            Log.d(TAG_DEBUG, "Dispatchers Unconfined 1: run on ${Thread.currentThread().name}")
            delay(3000)
            Log.d(TAG_DEBUG, "Dispatchers Unconfined 2: run on ${Thread.currentThread().name}")
        }

    }

    // 3
    fun testWithContext() {
        GlobalScope.launch(Dispatchers.IO) {
            Log.d(TAG_DEBUG, "Dispatchers Start: run on ${Thread.currentThread().name}")
            withContext(Dispatchers.Main) {
                Log.d(TAG_DEBUG, "withContext: run on ${Thread.currentThread().name}")
                binding.progressBar.isVisible = false
            }
            Log.d(TAG_DEBUG, "Dispatchers End: run on ${Thread.currentThread().name}")
        }


    }

    fun testLifecycleScope() {
        lifecycleScope.launch(Dispatchers.IO) {
            Log.d(TAG_DEBUG, "Dispatchers IO: run on ${Thread.currentThread().name}")
            withContext(Dispatchers.Main) {
                binding.progressBar.isVisible = false
            }
        }
    }

    fun testLifecycleScope2() {
        val mainScope = CoroutineScope(Dispatchers.Main)
        mainScope.launch(Dispatchers.IO) {
            Log.d(TAG_DEBUG, "Dispatchers IO: run on ${Thread.currentThread().name} - ${mainScope}")
            withContext(Dispatchers.Main) {
                binding.progressBar.isVisible = false
            }
        }
    }


    fun createFlow() = flow<String> {
        emit("10")
        emit("13")
    }
    // 4
    fun testJob() = GlobalScope.launch {
        val job = lifecycleScope.launch { // launch a new coroutine and keep a reference to its Job
            delay(4000L)
            println("World!")
        }
        println("Hello,")
        job.cancel() // wait until child coroutine completes
        println("Kotlin")
    }

    fun testJobCancelStart() {
        val jobParent = GlobalScope.launch() {
            val job = launch() { // launch a new coroutine and keep a reference to its Job
                delay(4000L)
                println("World!")
            }
            println("Hello,")
            job.cancel() // wait until child coroutine completes
            println("Kotlin")
        }
    }

    fun testRunBlockKing() {
        runBlocking {
            delay(1000)
            println("Xin chao")
        }
        println("hello")
    }

    fun testCorountineCancel() {
        val jobParent = GlobalScope.launch() {
            val startTime = System.currentTimeMillis()
            var nextTime = startTime
            val job = launch() { // launch a new coroutine and keep a reference to its Job
                var i = 0
                while (i < 4) {
                    ensureActive()
                    if (System.currentTimeMillis() >= nextTime && isActive) {
                        Log.d(TAG_DEBUG, "Index ${i++}")
                        nextTime += 1000L
                    }
                }
            }
            delay(1000)
            job.cancel()
//            job.cancelAndJoin() // wait until child coroutine completes
            job.invokeOnCompletion {
                if (it == null) {
                    Log.d(TAG_DEBUG, "job finish")
                } else {
                    Log.d(TAG_DEBUG, "job error: ${it.message}")
                }
            }
        }
        jobParent.invokeOnCompletion {
            if (it == null) {
                Log.d(TAG_DEBUG, "jobParent finish")
            } else {
                Log.d(TAG_DEBUG, "jobParent error: ${it.message}")
            }
        }
    }

    fun testParentJob() {
        val jobParent = GlobalScope.launch() {
            val job1 = launch {
                repeat(5) { i ->
                    delay(1000)
                    Log.d(TAG_DEBUG, "job1 -> $i")
                }
            }
            val job2 = launch {
                repeat(5) { i ->
                    delay(1000)
                    Log.d(TAG_DEBUG, "job2 -> $i")
                }
            }


            job1.invokeOnCompletion {
                if (it == null) {
                    Log.d(TAG_DEBUG, "job1 finish")
                } else {
                    Log.d(TAG_DEBUG, "job1 error: ${it.message}")
                }
            }

            job2.invokeOnCompletion {
                if (it == null) {
                    Log.d(TAG_DEBUG, "job2 finish")
                } else {
                    Log.d(TAG_DEBUG, "job2 error: ${it.message}")
                }
            }
//            delay(1000)
//            job2.cancel()
        }
        GlobalScope.launch {
            delay(1000)
            jobParent.cancel()
        }

        jobParent.invokeOnCompletion {
            if (it == null) {
                Log.d(TAG_DEBUG, "jobParent finish")
            } else {
                Log.d(TAG_DEBUG, "jobParent error: ${it.message}")
            }
        }
    }

    fun testTimeOut() {
        runBlocking {
            withTimeout(1300L) {
                repeat(1000) { i ->
                    println("I'm sleeping $i ...")
                    delay(500L)
                }
            }
        }
    }

    fun testRunBlocking() {
        runBlocking { // scope 1
            println("Coroutine scope is runBlocking") // line code 4
        }
        println("End") // line
    }

    fun testAsynAwait() {
        GlobalScope.launch {
//            val time = measureTimeMillis {
//                val one = printOne()
//                val two = printTwo()
//                println("The answer is ${one + two}")
//            }
            val time = measureTimeMillis {
                val one = async { printOne() }
                val two = async { printTwo() }
                println("The answer is ${one.await() + two.await()}")
            }
            println("Completed in $time ms")
        }
    }

    private suspend fun printOne(): Int {
        delay(4000L)
        return 10
    }

    private suspend fun printTwo(): Int {
        delay(1000L)
        return 20
    }

    fun testCoroutineScope() = runBlocking { // scope 1
        launch {       // coroutine 1 dung scop 1
            delay(200L)
            println("Task from runBlocking")   // line code 1
        }
        coroutineScope { // coroutine 2  co scope 2 rieng, khong dung scope cha, coroutineScope giống tính năng của runBlocking
            launch {   // coroutine 3
                delay(500L)
                println("Task from nested launch") // line code 2
            }
            delay(100L)
            println("Task from coroutine scope") // line code 3
        }
        println("Coroutine scope is over") // line code 4
    }

    fun testCorountineException() {
        runBlocking {
//            val value = lifecycleScope.async {
//                val list = mutableListOf<String>()
//                val item = list.get(1)
//                println("$item")
//            }
//            delay(3000)
//            println(value.await())

            GlobalScope.launch {
                try {
                    println("Throwing exception from launch")
                    throw IndexOutOfBoundsException()
                    println("Unreached")
                } catch (e: IndexOutOfBoundsException) {
                    println("Caught IndexOutOfBoundsException")
                }
            }

            val deferred = GlobalScope.async {
                println("Throwing exception from async")
                throw ArithmeticException()
                println("Unreached")
            }
            try {
                deferred.await()
                println("Unreached")
            } catch (e: ArithmeticException) {
                println("Caught ArithmeticException")
            }
        }

    }

    fun testCorountineException2() {
        val handler = CoroutineExceptionHandler { _, exception ->
            println("$exception")
        }
        runBlocking {
            val job = lifecycleScope.launch(handler) {
                launch { //job 1
                    println("job1 start")
                    throw IndexOutOfBoundsException()
                    println("job1 done")
                }
                launch { //job 2
                    println("job2 start")
                    throw NumberFormatException()
                    println("job2 done")
                }
            }

        }

    }

    fun testSupervisionJob() {
        val handler = CoroutineExceptionHandler { _, exception ->
            println("$exception")
        }
        runBlocking {
            val supervisor = SupervisorJob()
            val scopeSupervisorJob = CoroutineScope(supervisor)
            with(scopeSupervisorJob) {
                launch(handler) { //job 1
                    delay(2000)
                    println("job1 start")
                    throw IndexOutOfBoundsException()
                    println("job1 done")
                }
                launch(handler) { //job 2
                    delay(1000)
                    println("job2 start")
                    throw NumberFormatException()
                    println("job2 done")
                }
            }
        }
    }

    fun testSupervisionScope() {
        val handler = CoroutineExceptionHandler { _, exception ->
            println("$exception")
        }
        runBlocking {
            supervisorScope {
                launch(handler) { //job 1
                    delay(2000)
                    println("job1 start")
                    throw IndexOutOfBoundsException()
                    println("job1 done")
                }
                launch(handler) { //job 2
                    delay(1000)
                    println("job2 start")
                    throw NumberFormatException()
                    println("job2 done")
                }
            }
            println("job3 done")
        }

    }

    fun testTransform() {
        val numbersFlow = flowOf(1, 2, 3, 4, null, 5)
        val resultFlow = numbersFlow.filterNotNull() // Lấy các phần tử nhỏ hơn 4
        resultFlow.onEach { value ->
            println("$TAG_DEBUG - $value")
        }.launchIn(GlobalScope)
    }

    fun testLimit() {
        val numbersFlow = flowOf(1, 2, 3, 4, 5)
        val resultFlow = numbersFlow.dropWhile { it < 4 } // Lấy các phần tử nhỏ hơn 4

        resultFlow.onEach { value ->
            println("$TAG_DEBUG - $value")
        }.launchIn(GlobalScope)
    }

    fun createFlow2()= flow<Int> {
        emit(1)
        emit(2)
    }


    fun testZip() {
        val flow1 = flowOf(1, 2).map {
                delay(1000)
                it
            }
        val flow2 = flowOf("A", "B").map {
                delay(2000)
                it
            }
        val flow3 = flowOf(null).map {
                delay(1000)
                it
            }
        val flow4 = flowOf("A", "B", "C").transform {
                delay(2000)
                emit(it)
            }
//        println("$TAG_DEBUG - start")
        combine(flow1, flow2, flow3) { f1, f2, f3 ->
            println("$TAG_DEBUG - $f1 + $f2 + $f3")
        }.launchIn(GlobalScope)
//        flow1.combine(flow2) { f1, f2 ->
//            println("$TAG_DEBUG - $f1 + $f2")
//        }.launchIn(GlobalScope)
    }

    fun testMerge() {
        val flow1 = flowOf(3, 6).onStart { delay(1000) } // trc khi chạy sẽ delay 1s
            .transform {
                delay(2000)
                emit(it)
            }
        val flow2 = flowOf(8, 1).transform {
                delay(2000)
                emit(it)
            }
        merge(flow1, flow2).onEach {
                println("$TAG_DEBUG Merge: $it")
            }.filter { it < 4 }.onEach {
                println("$TAG_DEBUG Filter: $it")
            }.flowOn(Dispatchers.Main)
            .map {
                it + 1
            }.onEach {
                println("$TAG_DEBUG Map: $it")
            }.launchIn(GlobalScope)

//        val numbersFlow = flowOf(1, 2, 3)
        val flatMappedFlow = flow1.flatMapLatest { number ->
            flowOf("$number-A", "$number-B").onEach { delay(1000) }
        }
//        flow1.flatMapConcat {
//            flow2
//        }.onEach {
//            println("$TAG_DEBUG - $it")
//        }.launchIn(GlobalScope)
//
    }

    companion object {
        const val TAG_DEBUG = "MainActivity"
    }

}

fun main() {
    val flow1 = flowOf("3", "6").transform {
            println("flow1 - $it")
            delay(2000)
            emit(it)
        }
    val flow2 = flowOf("8", "1").onStart { delay(1000) }.transform {
            println("flow2 - $it")
            delay(2000)
            emit(it)
        }
    flow2.launchIn(GlobalScope)
    GlobalScope.launch {
        flow1.flatMapMerge { flow2 }.onEach {

        }.collect {
            println("$it")
        }
    }
}