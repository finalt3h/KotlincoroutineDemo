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
import com.example.kotlincorountine.databinding.LoadingDialogBinding
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

class LoadingDialog : DialogFragment(){


    private lateinit var binding: LoadingDialogBinding


    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onStart() {
        super.onStart()

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LoadingDialogBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }



    companion object {
        const val TAG = "DialogScope"
    }

}